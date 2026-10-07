package com.utkarsh.jobtracker.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class JobApplicationDto {

    private String company;

    private String roleTitle;

    private LocalDate followUpDate;
}