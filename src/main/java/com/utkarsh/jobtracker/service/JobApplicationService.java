package com.utkarsh.jobtracker.service;

import com.utkarsh.jobtracker.dto.JobApplicationDto;
import com.utkarsh.jobtracker.entity.JobApplication;
import com.utkarsh.jobtracker.entity.Reminder;
import com.utkarsh.jobtracker.repository.JobApplicationRepository;
import com.utkarsh.jobtracker.repository.ReminderRepository;
import com.utkarsh.jobtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository repo;
    private final UserRepository userRepo;
    private final ReminderRepository reminderRepo;
    private final SubscriptionService subscriptionService;

    public JobApplication create(String userId, JobApplicationDto dto) {
        subscriptionService.checkUsageLimit(userId);

        JobApplication app = new JobApplication();
        app.setUser(userRepo.getReferenceById(userId));
        app.setCompany(dto.getCompany());
        app.setRoleTitle(dto.getRoleTitle());
        app.setFollowUpDate(dto.getFollowUpDate());
        app.setStatus(JobApplication.ApplicationStatus.APPLIED);
        app.setAppliedDate(LocalDate.now());

        JobApplication savedApp = repo.save(app);

        if (dto.getFollowUpDate() != null) {
            Reminder reminder = new Reminder();
            reminder.setApplication(savedApp);
            reminder.setRemindAt(
                    dto.getFollowUpDate().atStartOfDay(ZoneId.systemDefault()).toInstant()
            );
            reminderRepo.save(reminder);
        }

        return savedApp;
    }

    public List<JobApplication> getAllForUser(String userId) {
        return repo.findByUserId(userId);
    }
}