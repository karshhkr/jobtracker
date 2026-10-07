package com.utkarsh.jobtracker.repository;

import com.utkarsh.jobtracker.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, String> {

    List<Reminder> findByRemindAtBeforeAndSentFalse(Instant now);
}