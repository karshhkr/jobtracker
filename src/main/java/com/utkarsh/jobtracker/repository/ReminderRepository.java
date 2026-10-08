package com.utkarsh.jobtracker.repository;

import com.utkarsh.jobtracker.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReminderRepository extends JpaRepository<Reminder, String> {

    @Query("select r from Reminder r join fetch r.jobApplication " +
            "where r.user.id = :userId and r.done = false and r.remindAt <= :today order by r.remindAt")
    List<Reminder> findDueForUser(@Param("userId") String userId, @Param("today") LocalDate today);

    @Query("select r from Reminder r join fetch r.user join fetch r.jobApplication " +
            "where r.notified = false and r.done = false and r.remindAt <= :today")
    List<Reminder> findPendingNotifications(@Param("today") LocalDate today);

    Optional<Reminder> findByIdAndUserId(String id, String userId);

    void deleteByJobApplicationId(String jobApplicationId);
}