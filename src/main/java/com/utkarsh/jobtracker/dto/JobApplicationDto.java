package com.utkarsh.jobtracker.dto;

import com.utkarsh.jobtracker.entity.JobApplication.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record JobApplicationDto(
        String id,
        @NotBlank @Size(max = 255) String company,
        @NotBlank @Size(max = 255) String roleTitle,
        @Size(max = 255) String location,
        @Size(max = 1000) String jobUrl,
        @NotNull Status status,
        LocalDate appliedDate,
        LocalDate followUpDate,
        @Size(max = 5000) String notes) {
}