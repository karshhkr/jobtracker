package com.utkarsh.jobtracker.service;

import com.utkarsh.jobtracker.dto.JobApplicationDto;
import com.utkarsh.jobtracker.entity.JobApplication;
import com.utkarsh.jobtracker.entity.JobApplication.Status;
import com.utkarsh.jobtracker.exception.ResourceNotFoundException;
import com.utkarsh.jobtracker.repository.JobApplicationRepository;
import com.utkarsh.jobtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository jobRepository;
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;
    private final ReminderService reminderService;

    @Transactional(readOnly = true)
    public List<JobApplicationDto> list(String userId) {
        return jobRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toDto).toList();
    }

    @Transactional
    public JobApplicationDto create(String userId, JobApplicationDto dto) {
        subscriptionService.assertCanCreate(userId);
        JobApplication job = new JobApplication();
        job.setUser(userRepository.getReferenceById(userId));
        apply(job, dto);
        jobRepository.save(job);
        reminderService.sync(job);
        return toDto(job);
    }

    @Transactional
    public JobApplicationDto update(String userId, String id, JobApplicationDto dto) {
        JobApplication job = find(userId, id);
        apply(job, dto);
        reminderService.sync(job);
        return toDto(job);
    }

    @Transactional
    public void delete(String userId, String id) {
        JobApplication job = find(userId, id);
        reminderService.deleteForJob(id);
        jobRepository.delete(job);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> stats(String userId) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (Status s : Status.values()) result.put(s.name(), 0L);
        for (Object[] row : jobRepository.countByStatus(userId)) {
            result.put(((Status) row[0]).name(), (Long) row[1]);
        }
        return result;
    }

    private JobApplication find(String userId, String id) {
        return jobRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
    }

    private void apply(JobApplication j, JobApplicationDto d) {
        j.setCompany(d.company().trim());
        j.setRoleTitle(d.roleTitle().trim());
        j.setLocation(blankToNull(d.location()));
        j.setJobUrl(blankToNull(d.jobUrl()));
        j.setStatus(d.status());
        j.setAppliedDate(d.appliedDate());
        j.setFollowUpDate(d.followUpDate());
        j.setNotes(blankToNull(d.notes()));
        // Applied hai aur follow-up nahi diya: default 7 din baad
        if (d.status() == Status.APPLIED && d.followUpDate() == null && d.appliedDate() != null) {
            j.setFollowUpDate(d.appliedDate().plusDays(7));
        }
    }

    private String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    private JobApplicationDto toDto(JobApplication j) {
        return new JobApplicationDto(j.getId(), j.getCompany(), j.getRoleTitle(), j.getLocation(), j.getJobUrl(),
                j.getStatus(), j.getAppliedDate(), j.getFollowUpDate(), j.getNotes());
    }
}