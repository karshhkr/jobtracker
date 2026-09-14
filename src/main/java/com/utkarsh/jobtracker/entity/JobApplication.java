package com.utkarsh.jobtracker.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name="job_applications")
@Data

public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
private String id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
private User user ;

    private String company;
    private  String roleTitle;

    @Enumerated(EnumType.company)
    private ApplicationStatus status ;  // APPLIED, INTERVIEW, OFFER, REJECTED
 private LocalDate appliedDate;
 private LocalDate followUpDate;
}
