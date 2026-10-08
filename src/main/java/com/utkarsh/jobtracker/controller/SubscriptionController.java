package com.utkarsh.jobtracker.controller;

import com.utkarsh.jobtracker.dto.SubscriptionStatusDto;
import com.utkarsh.jobtracker.service.PaymentService;
import com.utkarsh.jobtracker.service.SubscriptionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final PaymentService paymentService;

    public record VerifyRequest(@NotBlank String razorpay_order_id,
                                @NotBlank String razorpay_payment_id,
                                @NotBlank String razorpay_signature) {}

    @GetMapping("/status")
    public SubscriptionStatusDto status(Authentication auth) {
        return subscriptionService.status(auth.getName());
    }

    @PostMapping("/order")
    public Map<String, Object> order(Authentication auth) {
        return paymentService.createOrder(auth.getName());
    }

    @PostMapping("/verify")
    public SubscriptionStatusDto verify(Authentication auth, @Valid @RequestBody VerifyRequest req) {
        paymentService.verify(auth.getName(), req.razorpay_order_id(),
                req.razorpay_payment_id(), req.razorpay_signature());
        return subscriptionService.status(auth.getName());
    }

    /** Public endpoint: security signature verification se aati hai, raw body par. */
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody String payload,
                                        @RequestHeader("X-Razorpay-Signature") String signature) {
        paymentService.handleWebhook(payload, signature);
        return ResponseEntity.ok().build();
    }
}