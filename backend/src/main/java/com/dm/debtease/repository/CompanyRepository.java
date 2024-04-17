package com.dm.debtease.repository;

import com.dm.debtease.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {
    Company findByNameContainingIgnoreCase(String substring);
}
