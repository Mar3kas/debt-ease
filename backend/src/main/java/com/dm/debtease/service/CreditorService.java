package com.dm.debtease.service;

import com.dm.debtease.model.Creditor;
import com.dm.debtease.model.dto.CreditorDTO;

import java.util.List;

public interface CreditorService {
    List<Creditor> getAllCreditors();

    Creditor getCreditorById(int id);

    Creditor getCreditorByUsername(String username);

    Creditor editCreditorById(CreditorDTO creditorDTO, int id);

    Creditor createCreditor(CreditorDTO creditorDTO);

    boolean deleteCreditorById(int id);
}
