package com.dm.debtease.service.impl;

import com.dm.debtease.model.Creditor;
import com.dm.debtease.model.CustomUser;
import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.Role;
import com.dm.debtease.model.dto.CreditorDTO;
import com.dm.debtease.repository.CreditorRepository;
import com.dm.debtease.service.CreditorService;
import com.dm.debtease.service.DebtCaseService;
import com.dm.debtease.utils.Constants;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CreditorServiceImpl implements CreditorService {
    private final CreditorRepository creditorRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final DebtCaseService debtCaseService;

    @Override
    public List<Creditor> getAllCreditors() {
        return creditorRepository.findAll();
    }

    @Override
    public Creditor getCreditorById(int id) {
        Optional<Creditor> optionalCreditor = creditorRepository.findById(id);
        return optionalCreditor.orElseThrow(
                () -> new EntityNotFoundException(String.format(Constants.CREDITOR_NOT_FOUND, id)));
    }

    @Override
    public Creditor getCreditorByUsername(String username) {
        Optional<Creditor> optionalCreditor = creditorRepository.findByUserUsername(username);
        return optionalCreditor.orElse(null);
    }

    @Override
    public Creditor editCreditorById(CreditorDTO creditorDTO, int id) {
        Optional<Creditor> optionalCreditor = creditorRepository.findById(id);
        if (optionalCreditor.isPresent()) {
            Creditor creditor = optionalCreditor.get();
            if (creditorDTO.getName() != null) {
                creditor.setName(creditorDTO.getName());
            }
            if (creditorDTO.getAddress() != null) {
                creditor.setAddress(creditorDTO.getAddress());
            }
            if (creditorDTO.getPhoneNumber() != null) {
                creditor.setPhoneNumber(creditorDTO.getPhoneNumber());
            }
            if (creditorDTO.getEmail() != null) {
                creditor.setEmail(creditorDTO.getEmail());
            }
            if (creditorDTO.getAccountNumber() != null) {
                creditor.setAccountNumber(creditorDTO.getAccountNumber());
            }
            return creditorRepository.save(creditor);
        }
        throw new EntityNotFoundException(String.format(Constants.CREDITOR_NOT_FOUND, id));
    }

    @Override
    public Creditor createCreditor(CreditorDTO creditorDTO) {
        Creditor creditor = new Creditor();
        creditor.setName(creditorDTO.getName());
        creditor.setAddress(creditorDTO.getAddress());
        creditor.setPhoneNumber(creditorDTO.getPhoneNumber());
        creditor.setEmail(creditorDTO.getEmail());
        creditor.setAccountNumber(creditorDTO.getAccountNumber());
        CustomUser customUser = new CustomUser();
        customUser.setUsername(!creditorDTO.getUsername().isBlank() ? creditorDTO.getUsername() : creditor.getName());
        customUser.setPassword(bCryptPasswordEncoder.encode(!creditorDTO.getPassword().isBlank() ? creditorDTO.getPassword() : creditor.getName()));
        customUser.setRole(Role.CREDITOR);
        creditor.setUser(customUser);
        return creditorRepository.save(creditor);
    }

    @Override
    public boolean deleteCreditorById(int id) {
        Optional<Creditor> optionalCreditor = creditorRepository.findById(id);
        if (optionalCreditor.isPresent()) {
            List<DebtCase> debtCases = debtCaseService.getAllDebtCases()
                    .stream()
                    .filter(debtCase -> debtCase.getCreditor().getId() == id).toList();
            if (!debtCases.isEmpty()) {
                return false;
            }
            creditorRepository.deleteById(id);
            return true;
        }
        throw new EntityNotFoundException(String.format(Constants.CREDITOR_NOT_FOUND, id));
    }
}
