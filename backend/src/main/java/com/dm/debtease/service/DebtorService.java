package com.dm.debtease.service;

import com.dm.debtease.model.Debtor;
import com.dm.debtease.model.dto.DebtorDTO;

import java.util.List;

public interface DebtorService {
    List<Debtor> getAllDebtors();

    Debtor getDebtorById(int id);

    Debtor getDebtorByUsername(String username);

    Debtor getDebtorByNameAndSurname(String name, String surname);

    Debtor editDebtorById(DebtorDTO debtorDTO, int id);

    Debtor createDebtor(DebtorDTO debtorDTO);

    boolean deleteDebtorById(int id);
}
