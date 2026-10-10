package com.utkarsh.jobtracker.service;

import com.utkarsh.jobtracker.dto.CompanyDto;
import com.utkarsh.jobtracker.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public List<CompanyDto> list() {
        return companyRepository.findAllByOrderByNameAsc().stream()
                .map(c -> new CompanyDto(c.getName(), c.getCategory()))
                .toList();
    }
}