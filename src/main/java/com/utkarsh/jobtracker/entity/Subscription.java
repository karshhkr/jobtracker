package com.utkarsh.jobtracker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "subscriptions")
@Data
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    private Plan plan = Plan.FREE;

    private String razorpaySubId;

    @Enumerated(EnumType.STRING)
    private SubStatus status = SubStatus.ACTIVE;

    private Instant startDate = Instant.now();

    private Instant endDate;

    public enum Plan {
        FREE, PRO
    }

    public enum SubStatus {
        ACTIVE, EXPIRED, CANCELLED
    }
}