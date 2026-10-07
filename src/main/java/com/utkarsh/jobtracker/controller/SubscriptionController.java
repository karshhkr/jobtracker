package com.utkarsh.jobtracker.controller;

import com.razorpay.RazorpayException;
import com.utkarsh.jobtracker.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final PaymentService paymentService;

    @PostMapping("/checkout")
    public Map<String, String> createOrder() throws RazorpayException {
        String orderId = paymentService.createOrder();
        return Map.of("orderId", orderId);
    }

    // Webhook signature verification abhi add karni hai — Razorpay docs ke mutabik
    @PostMapping("/webhook")
    public void handleWebhook() {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        paymentService.activatePro(userId);
    }
}