package com.dm.debtease.service.impl;

import com.dm.debtease.model.Debtor;
import com.dm.debtease.model.dto.DebtorDTO;
import com.dm.debtease.repository.DebtorRepository;
import com.dm.debtease.service.DebtorService;
import com.dm.debtease.utils.Constants;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DebtorServiceImpl implements DebtorService {
    private final DebtorRepository debtorRepository;

    @Override
    public List<Debtor> getAllDebtors() {
        return debtorRepository.findAll();
    }

    public Debtor getDebtorById(int id) {
        Optional<Debtor> optionalDebtor = debtorRepository.findById(id);
        return optionalDebtor.orElseThrow(
                () -> new EntityNotFoundException(String.format(Constants.DEBTOR_NOT_FOUND, id)));
    }

    @Override
    public Debtor getDebtorByUsername(String username) {
        Optional<Debtor> optionalDebtor = debtorRepository.findByUserUsername(username);
        return optionalDebtor.orElse(null);
    }

    @Override
    public Debtor getDebtorByNameAndSurname(String name, String surname) {
        Optional<Debtor> existingDebtor = debtorRepository.findByNameAndSurname(name, surname);
        return existingDebtor.orElse(null);
    }

    @Override
    public Debtor editDebtorById(DebtorDTO debtorDTO, int id) {
        Optional<Debtor> optionalDebtor = debtorRepository.findById(id);
        if (optionalDebtor.isPresent()) {
            Debtor debtor = optionalDebtor.get();
            if (debtorDTO.getName() != null) {
                debtor.setName(debtorDTO.getName());
            }
            if (debtorDTO.getSurname() != null) {
                debtor.setSurname(debtorDTO.getSurname());
            }
            if (debtorDTO.getEmail() != null) {
                debtor.setEmail(debtorDTO.getEmail());
            }
            if (debtorDTO.getPhoneNumber() != null) {
                debtor.setPhoneNumber(debtorDTO.getPhoneNumber());
            }
            return debtorRepository.save(debtor);
        }
        throw new EntityNotFoundException(String.format(Constants.DEBTOR_NOT_FOUND, id));
    }

    @Override
    public Debtor createDebtor(DebtorDTO debtorDTO) {
        Debtor debtor = new Debtor();
        if (debtorDTO.getName() != null) {
            debtor.setName(debtorDTO.getName());
        }
        if (debtorDTO.getSurname() != null) {
            debtor.setSurname(debtorDTO.getSurname());
        }
        if (debtorDTO.getEmail() != null) {
            debtor.setEmail(debtorDTO.getEmail());
        }
        if (debtorDTO.getPhoneNumber() != null) {
            debtor.setPhoneNumber(debtorDTO.getPhoneNumber());
        }
        return debtorRepository.save(debtor);
    }

    @Override
    public boolean deleteDebtorById(int id) {
        Optional<Debtor> optionalDebtor = debtorRepository.findById(id);
        if (optionalDebtor.isPresent()) {
            debtorRepository.deleteById(id);
            return true;
        }
        throw new EntityNotFoundException(String.format(Constants.DEBTOR_NOT_FOUND, id));
    }
}
