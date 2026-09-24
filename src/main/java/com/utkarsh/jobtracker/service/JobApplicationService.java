package com.utkarsh.jobtracker.service;

import com.utkarsh.jobtracker.entity.ApplicationStatus;
import com.utkarsh.jobtracker.entity.JobApplication;
import com.utkarsh.jobtracker.repository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor


public class JobApplicationService {
    private final JobApplicationRepository repo;
    private final SubscriptionService subscriptionService;

    public JobApplication create(String userId, JobApplication jobApplicationDto dto){
        subscriptionService.checkUsageLimit(userId); //week 4
        JobApplication app =new JobApplication();
    app.setUser(userRepo.getReferenceById(userId));
    app.setCompany(dto.getComapny());
    app.setRoleTitle(dto.getRoleTitle());
    app.setStatus(ApplicationStatus.APPLIED);
    app.setAppliedDate(LocalDate.now());
    return repo.save(app);





    }
    public List<JobApplication> getAllForUser(String userId) {
        return repo.findByUserId(userId);
    }
}
