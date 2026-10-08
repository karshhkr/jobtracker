package com.utkarsh.jobtracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "subscriptions")
@Getter @Setter
public class Subscription {

    public enum Plan { FREE, PRO }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Plan plan = Plan.FREE;

    private Instant expiresAt;

    public boolean isActivePro() {
        return plan == Plan.PRO && expiresAt != null && expiresAt.isAfter(Instant.now());
    }
}