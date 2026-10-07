package com.utkarsh.jobtracker.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.utkarsh.jobtracker.entity.Subscription;
import com.utkarsh.jobtracker.repository.SubscriptionRepository;
import com.utkarsh.jobtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final SubscriptionRepository subRepo;
    private final UserRepository userRepo;

    public String createOrder() throws RazorpayException {
        JSONObject options = new JSONObject();
        options.put("amount", 49900); // ₹499 in paise
        options.put("currency", "INR");

        Order order = razorpayClient.orders.create(options);
        return order.get("id");
    }

    public void activatePro(String userId) {
        Subscription sub = subRepo.findByUserId(userId).orElseGet(() -> {
            Subscription s = new Subscription();
            s.setUser(userRepo.getReferenceById(userId));
            return s;
        });

        sub.setPlan(Subscription.Plan.PRO);
        sub.setStatus(Subscription.SubStatus.ACTIVE);
        subRepo.save(sub);
    }
}