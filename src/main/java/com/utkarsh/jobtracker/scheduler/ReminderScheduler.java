package com.utkarsh.jobtracker.scheduler;

import com.utkarsh.jobtracker.entity.Reminder;
import com.utkarsh.jobtracker.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final ReminderRepository reminderRepo;
    private final JavaMailSender mailSender;

    @Scheduled(cron = "0 0 * * * *")
    public void sendDueReminders() {
        List<Reminder> due = reminderRepo.findByRemindAtBeforeAndSentFalse(Instant.now());

        for (Reminder r : due) {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(r.getApplication().getUser().getEmail());
            mail.setSubject("Follow up: " + r.getApplication().getCompany());
            mail.setText("Time to follow up on your application to " + r.getApplication().getCompany());
            mailSender.send(mail);

            r.setSent(true);
            reminderRepo.save(r);
        }
    }
}