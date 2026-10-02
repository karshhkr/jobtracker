package com.utkarsh.jobtracker.service;

import com.utkarsh.jobtracker.entity.Subscription;
import com.utkarsh.jobtracker.exception.UsageLimitExceededException;
import com.utkarsh.jobtracker.repository.JobApplicationRepository;
import com.utkarsh.jobtracker.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private static final int FREE_TIER_LIMIT = 20;

    private final SubscriptionRepository subRepo;
    private final JobApplicationRepository appRepo;

    public void checkUsageLimit(String userId) {
        Subscription sub = subRepo.findByUserId(userId).orElse(null);

        boolean isPro = sub != null
                && sub.getPlan() == Subscription.Plan.PRO
                && sub.getStatus() == Subscription.SubStatus.ACTIVE;

        if (isPro) {
            return;
        }

        long count = appRepo.countByUserId(userId);
        if (count >= FREE_TIER_LIMIT) {
            throw new UsageLimitExceededException("Free tier limit reached. Upgrade to Pro.");
        }
    }
}