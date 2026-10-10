package com.utkarsh.jobtracker.repository;

import com.utkarsh.jobtracker.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, String> {
    List<Company> findAllByOrderByNameAsc();
}