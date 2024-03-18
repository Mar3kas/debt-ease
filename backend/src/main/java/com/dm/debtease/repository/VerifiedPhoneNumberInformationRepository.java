package com.dm.debtease.repository;

import com.dm.debtease.model.VerifiedPhoneNumberInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerifiedPhoneNumberInformationRepository extends JpaRepository<VerifiedPhoneNumberInformation, Integer> {
}
