package com.dm.debtease.service.impl;

import com.dm.debtease.model.Debtor;
import com.dm.debtease.model.dto.DebtorDTO;
import com.dm.debtease.repository.DebtorRepository;
import com.dm.debtease.service.DebtorService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DebtorServiceImpl implements DebtorService {
    private final DebtorRepository debtorRepository;

    @Autowired
    public DebtorServiceImpl(DebtorRepository debtorRepository) {
        this.debtorRepository = debtorRepository;
    }

    @Override
    public List<Debtor> getAllDebtors() {
        return debtorRepository.findAll();
    }

    public Debtor getDebtorById(int id) {
        Optional<Debtor> optionalDebtor = debtorRepository.findById(id);

        return optionalDebtor.orElseThrow(() -> new EntityNotFoundException("Debtor not found with id " + id));
    }

    @Override
    public Debtor getDebtorByUsername(String username) {
        Optional<Debtor> optionalDebtor = debtorRepository.findByUserUsername(username);

        return optionalDebtor.orElseThrow(() -> new EntityNotFoundException("Debtor not found with username " + username));
    }

    @Override
    public Debtor editDebtorById(DebtorDTO debtorDTO, int id, int debtcaseId, int creditorId) {
        Optional<Debtor> optionalDebtor = debtorRepository.findByIdAndDebtCase_IdAndDebtCase_Creditor_Id(id, debtcaseId, creditorId);

        if (optionalDebtor.isPresent()) {
            Debtor debtor = optionalDebtor.get();
            if (Objects.nonNull(debtorDTO.getName())) {
                debtor.setName(debtorDTO.getName());
            }
            if (Objects.nonNull(debtorDTO.getSurname())) {
                debtor.setSurname(debtorDTO.getSurname());
            }
            if (Objects.nonNull(debtorDTO.getEmail())) {
                debtor.setEmail(debtorDTO.getEmail());
            }
            if (Objects.nonNull(debtorDTO.getPhoneNumber())) {
                debtor.setPhoneNumber(debtorDTO.getPhoneNumber());
            }

            return debtorRepository.save(debtor);
        }

        throw new EntityNotFoundException("Debtor not found with id " + id);
    }

    @Override
    public Debtor editDebtorById(DebtorDTO debtorDTO, int id) {
        Optional<Debtor> optionalDebtor = debtorRepository.findById(id);

        if (optionalDebtor.isPresent()) {
            Debtor debtor = optionalDebtor.get();
            if (Objects.nonNull(debtorDTO.getName())) {
                debtor.setName(debtorDTO.getName());
            }
            if (Objects.nonNull(debtorDTO.getSurname())) {
                debtor.setSurname(debtorDTO.getSurname());
            }
            if (Objects.nonNull(debtorDTO.getEmail())) {
                debtor.setEmail(debtorDTO.getEmail());
            }
            if (Objects.nonNull(debtorDTO.getPhoneNumber())) {
                debtor.setPhoneNumber(debtorDTO.getPhoneNumber());
            }

            return debtorRepository.save(debtor);
        }

        throw new EntityNotFoundException("Debtor not found with id " + id);
    }

    @Override
    public boolean deleteDebtorById(int id) {
        Optional<Debtor> optionalDebtor = debtorRepository.findById(id);
        if (optionalDebtor.isPresent()) {
            debtorRepository.deleteById(id);

            return true;
        }

        throw new EntityNotFoundException("Debtor not found with id " + id);
    }

    @Override
    public boolean deleteDebtorById(int id, int debtcaseId, int creditorId) {
        Optional<Debtor> optionalDebtor = debtorRepository.findByIdAndDebtCase_IdAndDebtCase_Creditor_Id(id, debtcaseId, creditorId);
        if (optionalDebtor.isPresent()) {
            debtorRepository.deleteById(id);

            return true;
        }

        throw new EntityNotFoundException("Debtor not found with id " + id);
    }
}
