package com.utkarsh.jobtracker.repository;

import com.utkarsh.jobtracker.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, String> {

    List<JobApplication> findByUserId(String userId);

    long countByUserId(String userId);

    long countByUserIdAndAppliedDateAfter(String userId, LocalDate date);
}