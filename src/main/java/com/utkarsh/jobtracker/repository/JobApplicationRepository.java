package com.utkarsh.jobtracker.repository;

import com.utkarsh.jobtracker.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, String> {

    List<JobApplication> findByUserIdOrderByCreatedAtDesc(String userId);

    /** Tenant isolation: id akeli kabhi kaafi nahi, hamesha userId ke saath. */
    Optional<JobApplication> findByIdAndUserId(String id, String userId);

    long countByUserId(String userId);

    @Query("select j.status, count(j) from JobApplication j where j.user.id = :userId group by j.status")
    List<Object[]> countByStatus(@Param("userId") String userId);
}