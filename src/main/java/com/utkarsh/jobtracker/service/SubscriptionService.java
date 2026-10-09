package com.utkarsh.jobtracker.service;

import com.utkarsh.jobtracker.dto.SubscriptionStatusDto;
import com.utkarsh.jobtracker.entity.Subscription;
import com.utkarsh.jobtracker.entity.Subscription.Plan;
import com.utkarsh.jobtracker.exception.ResourceNotFoundException;
import com.utkarsh.jobtracker.exception.UsageLimitExceededException;
import com.utkarsh.jobtracker.repository.JobApplicationRepository;
import com.utkarsh.jobtracker.repository.SubscriptionRepository;
import com.utkarsh.jobtracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Service
public class SubscriptionService {

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final JobApplicationRepository jobRepository;
    private final int freeLimit;
    private final long pricePaise;
    private final int proDays;
    private final boolean betaFree;

    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                               UserRepository userRepository,
                               JobApplicationRepository jobRepository,
                               @Value("${app.free-limit}") int freeLimit,
                               @Value("${app.pro-price-paise}") long pricePaise,
                               @Value("${app.pro-days}") int proDays,
                               @Value("${app.mock-payments:false}") boolean betaFree) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.freeLimit = freeLimit;
        this.pricePaise = pricePaise;
        this.proDays = proDays;
        this.betaFree = betaFree;
    }

    @Transactional(readOnly = true)
    public boolean isPro(String userId) {
        return subscriptionRepository.findByUserId(userId).map(Subscription::isActivePro).orElse(false);
    }

    @Transactional(readOnly = true)
    public void assertCanCreate(String userId) {
        if (!isPro(userId) && jobRepository.countByUserId(userId) >= freeLimit) {
            throw new UsageLimitExceededException(
                    "Free plan allows " + freeLimit + " applications. Upgrade to Pro for unlimited tracking.");
        }
    }

    @Transactional(readOnly = true)
    public SubscriptionStatusDto status(String userId) {
        Subscription sub = subscriptionRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));
        boolean pro = sub.isActivePro();
        return new SubscriptionStatusDto(
                pro ? Plan.PRO.name() : Plan.FREE.name(), pro,
                pro ? sub.getExpiresAt().atZone(IST).toLocalDate() : null,
                jobRepository.countByUserId(userId),
                pro ? -1 : freeLimit, pro, pricePaise, betaFree);
    }

    /** Pro deta hai ya extend karta hai. Pehle se Pro ho to purani expiry ke baad se jodta hai. */
    @Transactional
    public void activatePro(String userId) {
        Subscription sub = subscriptionRepository.findByUserId(userId).orElseGet(() -> {
            Subscription s = new Subscription();
            s.setUser(userRepository.getReferenceById(userId));
            return s;
        });
        Instant base = sub.isActivePro() ? sub.getExpiresAt() : Instant.now();
        sub.setPlan(Plan.PRO);
        sub.setExpiresAt(base.plus(proDays, ChronoUnit.DAYS));
        subscriptionRepository.save(sub);
    }

    public long getPricePaise() {
        return pricePaise;
    }
}