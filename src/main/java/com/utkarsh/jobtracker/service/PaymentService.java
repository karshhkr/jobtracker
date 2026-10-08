package com.utkarsh.jobtracker.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.utkarsh.jobtracker.entity.Payment;
import com.utkarsh.jobtracker.entity.User;
import com.utkarsh.jobtracker.exception.ResourceNotFoundException;
import com.utkarsh.jobtracker.repository.PaymentRepository;
import com.utkarsh.jobtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RazorpayClient razorpay;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    @Value("${razorpay.webhook-secret}")
    private String webhookSecret;

    @Transactional
    public Map<String, Object> createOrder(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        long amount = subscriptionService.getPricePaise();
        try {
            JSONObject options = new JSONObject();
            options.put("amount", amount);
            options.put("currency", "INR");
            options.put("receipt", "jt_" + System.currentTimeMillis());
            Order order = razorpay.orders.create(options);
            String orderId = order.get("id");

            Payment p = new Payment();
            p.setUser(user);
            p.setRazorpayOrderId(orderId);
            p.setAmount(amount);
            paymentRepository.save(p);

            return Map.of("orderId", orderId, "amount", amount, "currency", "INR",
                    "keyId", keyId, "name", user.getName(), "email", user.getEmail());
        } catch (RazorpayException e) {
            throw new IllegalStateException("Could not create payment order: " + e.getMessage(), e);
        }
    }

    /** Browser handler se aaya payment: signature server par verify hota hai. Idempotent. */
    @Transactional
    public void verify(String userId, String orderId, String paymentId, String signature) {
        Payment p = paymentRepository.lockByOrderId(orderId)
                .filter(x -> x.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (p.getStatus() == Payment.Status.PAID) {
            return;
        }
        try {
            JSONObject attrs = new JSONObject();
            attrs.put("razorpay_order_id", orderId);
            attrs.put("razorpay_payment_id", paymentId);
            attrs.put("razorpay_signature", signature);
            if (!Utils.verifyPaymentSignature(attrs, keySecret)) {
                throw new RazorpayException("signature mismatch");
            }
        } catch (RazorpayException e) {
            p.setStatus(Payment.Status.FAILED);
            throw new IllegalArgumentException("Payment verification failed");
        }
        confirm(p, paymentId);
    }

    /** Razorpay server se webhook: user tab band kar de tab bhi Pro mil jata hai. */
    @Transactional
    public void handleWebhook(String payload, String signature) {
        try {
            if (!Utils.verifyWebhookSignature(payload, signature, webhookSecret)) {
                throw new RazorpayException("bad signature");
            }
        } catch (RazorpayException e) {
            throw new IllegalArgumentException("Invalid webhook signature");
        }
        JSONObject root = new JSONObject(payload);
        if (!"payment.captured".equals(root.optString("event"))) {
            return;
        }
        JSONObject entity = root.getJSONObject("payload").getJSONObject("payment").getJSONObject("entity");
        paymentRepository.lockByOrderId(entity.getString("order_id")).ifPresentOrElse(p -> {
            if (p.getStatus() != Payment.Status.PAID) {
                confirm(p, entity.getString("id"));
            }
        }, () -> log.warn("Webhook for unknown order {}", entity.optString("order_id")));
    }

    private void confirm(Payment p, String paymentId) {
        p.setStatus(Payment.Status.PAID);
        p.setRazorpayPaymentId(paymentId);
        subscriptionService.activatePro(p.getUser().getId());
    }
}