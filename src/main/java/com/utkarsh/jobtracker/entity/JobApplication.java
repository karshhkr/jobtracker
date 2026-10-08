package com.utkarsh.jobtracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "job_applications")
@Getter @Setter
public class JobApplication {

    public enum Status { WISHLIST, APPLIED, INTERVIEW, OFFER, REJECTED }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String roleTitle;

    private String location;

    @Column(length = 1000)
    private String jobUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.APPLIED;

    private LocalDate appliedDate;
    private LocalDate followUpDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();
}