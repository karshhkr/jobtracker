package com.utkarsh.jobtracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "reminders")
@Getter @Setter
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_application_id", nullable = false)
    private JobApplication jobApplication;

    @Column(nullable = false)
    private LocalDate remindAt;

    private String message;

    /** Scheduler isse process kar chuka (Pro ko email gaya). */
    private boolean notified = false;

    /** User ne dashboard par dismiss kar diya. */
    private boolean done = false;
}