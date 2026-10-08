package com.utkarsh.jobtracker.scheduler;

import com.utkarsh.jobtracker.service.ReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final ReminderService reminderService;

    @Scheduled(cron = "${app.reminder-cron}", zone = "Asia/Kolkata")
    public void run() {
        log.info("Reminder run complete: {} processed", reminderService.processPending());
    }
}