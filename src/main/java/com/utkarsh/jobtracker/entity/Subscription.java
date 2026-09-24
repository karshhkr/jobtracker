package com.utkarsh.jobtracker.entity;

import com.razorpay.Plan;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name ="subscription")
@Data

public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

     @OneToMany(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,unique = true)
    private User user;
             @Enumerated(EnumType.STRING)
             private Plan plan =Plan.FREE;

             private String razorPaySubId;

             @Enumerated(EnumType.STRING)
             private SubStatus status= SubStatus.ACTIVE;

             private Instant startDate= Instant.now();

             private Instant endDate;

             public enum Plan {
                 Free , Pro
             }
             public enum SubStatus {
                 ACTIVE, EXPIRED, CANCELLED
             }



}
