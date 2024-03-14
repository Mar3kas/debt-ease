package com.dm.debtease.repository;

import com.dm.debtease.model.CompanyInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyInformationRepository extends JpaRepository<CompanyInformation, Integer> {
    CompanyInformation findByNameContainingIgnoreCase(String substring);
}
