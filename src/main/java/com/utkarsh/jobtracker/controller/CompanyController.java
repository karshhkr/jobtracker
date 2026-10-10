package com.utkarsh.jobtracker.controller;

import com.utkarsh.jobtracker.dto.CompanyDto;
import com.utkarsh.jobtracker.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    public List<CompanyDto> list() {
        return companyService.list();
    }
}