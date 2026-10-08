package com.utkarsh.jobtracker.service;

import com.utkarsh.jobtracker.dto.ReminderDto;
import com.utkarsh.jobtracker.entity.JobApplication;
import com.utkarsh.jobtracker.entity.JobApplication.Status;
import com.utkarsh.jobtracker.entity.Reminder;
import com.utkarsh.jobtracker.exception.ResourceNotFoundException;
import com.utkarsh.jobtracker.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderService {

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    private final ReminderRepository reminderRepository;
    private final SubscriptionService subscriptionService;
    private final ObjectProvider<JavaMailSender> mailSender;

    @Value("${app.mail-from}")
    private String mailFrom;

    /** Har job ke liye sirf ek pending reminder, follow-up date se chalta hai. */
    @Transactional
    public void sync(JobApplication job) {
        reminderRepository.deleteByJobApplicationId(job.getId());
        boolean open = job.getStatus() == Status.APPLIED || job.getStatus() == Status.INTERVIEW;
        if (job.getFollowUpDate() == null || !open) {
            return;
        }
        Reminder r = new Reminder();
        r.setUser(job.getUser());
        r.setJobApplication(job);
        r.setRemindAt(job.getFollowUpDate());
        r.setMessage("Follow up with " + job.getCompany() + " about the " + job.getRoleTitle() + " role");
        reminderRepository.save(r);
    }

    @Transactional
    public void deleteForJob(String jobId) {
        reminderRepository.deleteByJobApplicationId(jobId);
    }

    @Transactional(readOnly = true)
    public List<ReminderDto> due(String userId) {
        return reminderRepository.findDueForUser(userId, LocalDate.now(IST)).stream()
                .map(r -> new ReminderDto(r.getId(), r.getJobApplication().getId(),
                        r.getJobApplication().getCompany(), r.getJobApplication().getRoleTitle(),
                        r.getRemindAt(), r.getMessage()))
                .toList();
    }

    @Transactional
    public void markDone(String userId, String id) {
        Reminder r = reminderRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found"));
        r.setDone(true);
    }

    /** Scheduler roz chalata hai. Pro ko email, free user ko sirf dashboard par dikhta hai. */
    @Transactional
    public int processPending() {
        int processed = 0;
        for (Reminder r : reminderRepository.findPendingNotifications(LocalDate.now(IST))) {
            try {
                if (subscriptionService.isPro(r.getUser().getId())) {
                    sendEmail(r.getUser().getEmail(), r);
                }
                r.setNotified(true);
                processed++;
            } catch (Exception e) {
                log.warn("Reminder {} email failed, kal dobara try hoga: {}", r.getId(), e.getMessage());
            }
        }
        return processed;
    }

    private void sendEmail(String to, Reminder r) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null) {
            log.info("[mail not configured] would email {}: {}", to, r.getMessage());
            return;
        }
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(mailFrom);
        msg.setTo(to);
        msg.setSubject("JobTrackr reminder: " + r.getJobApplication().getCompany());
        msg.setText(r.getMessage() + ".\n\nA short, polite follow-up improves your chance of a reply.\n\n- JobTrackr");
        sender.send(msg);
    }
}