package com.utkarsh.jobtracker.dto;

import java.time.LocalDate;

public record ReminderDto(String id, String jobId, String company, String roleTitle,
                          LocalDate remindAt, String message) {
}