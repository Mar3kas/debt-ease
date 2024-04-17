package com.dm.debtease.repository;

import com.dm.debtease.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    List<Payment> findByDebtCase_Debtor_User_Username(String username);
}
