package com.utkarsh.jobtracker.controller;

import com.utkarsh.jobtracker.dto.JobApplicationDto;
import com.utkarsh.jobtracker.dto.ReminderDto;
import com.utkarsh.jobtracker.service.JobApplicationService;
import com.utkarsh.jobtracker.service.ReminderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** auth.getName() yahan userId hai (JWT subject). */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService jobService;
    private final ReminderService reminderService;

    @GetMapping("/jobs")
    public List<JobApplicationDto> list(Authentication auth) {
        return jobService.list(auth.getName());
    }

    @PostMapping("/jobs")
    public ResponseEntity<JobApplicationDto> create(Authentication auth, @Valid @RequestBody JobApplicationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jobService.create(auth.getName(), dto));
    }

    @PutMapping("/jobs/{id}")
    public JobApplicationDto update(Authentication auth, @PathVariable String id,
                                    @Valid @RequestBody JobApplicationDto dto) {
        return jobService.update(auth.getName(), id, dto);
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable String id) {
        jobService.delete(auth.getName(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/jobs/stats")
    public Map<String, Long> stats(Authentication auth) {
        return jobService.stats(auth.getName());
    }

    @GetMapping("/reminders")
    public List<ReminderDto> reminders(Authentication auth) {
        return reminderService.due(auth.getName());
    }

    @PostMapping("/reminders/{id}/done")
    public ResponseEntity<Void> done(Authentication auth, @PathVariable String id) {
        reminderService.markDone(auth.getName(), id);
        return ResponseEntity.noContent().build();
    }
}