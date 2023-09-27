package com.dm.debtease.service.impl;

import com.dm.debtease.model.Creditor;
import com.dm.debtease.model.CustomUser;
import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.Role;
import com.dm.debtease.model.dto.CreditorDTO;
import com.dm.debtease.repository.CreditorRepository;
import com.dm.debtease.repository.CustomUserRepository;
import com.dm.debtease.repository.DebtCaseRepository;
import com.dm.debtease.repository.RoleRepository;
import com.dm.debtease.service.CreditorService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CreditorServiceImpl implements CreditorService {
    private final CreditorRepository creditorRepository;
    private final CustomUserRepository customUserRepository;
    private final RoleRepository roleRepository;
    private final DebtCaseRepository debtCaseRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    public CreditorServiceImpl(CreditorRepository creditorRepository, CustomUserRepository customUserRepository,
                               RoleRepository roleRepository, DebtCaseRepository debtCaseRepository,
                               BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.creditorRepository = creditorRepository;
        this.customUserRepository = customUserRepository;
        this.roleRepository = roleRepository;
        this.debtCaseRepository = debtCaseRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
    @Override
    public List<Creditor> getAllCreditors() {
        return creditorRepository.findAll();
    }
    @Override
    public Creditor getCreditorById(int id) {
        Optional<Creditor> optionalCreditor = creditorRepository.findById(id);

        return optionalCreditor.orElseThrow(() -> new EntityNotFoundException("Creditor not found with ID: " + id));
    }
    @Override
    public Creditor getCreditorByUsername(String username) {
        Optional<Creditor> optionalCreditor = creditorRepository.findByUserUsername(username);

        return optionalCreditor.orElseThrow(() -> new EntityNotFoundException("Creditor not found with username: " + username));
    }
    @Override
    public Creditor editCreditorById(CreditorDTO creditorDTO, int id) {
        Optional<Creditor> optionalCreditor = creditorRepository.findById(id);
        if (optionalCreditor.isPresent()) {
            Creditor creditor = optionalCreditor.get();
            if (Objects.nonNull(creditorDTO.getName())) {
                creditor.setName(creditorDTO.getName());
            }
            if (Objects.nonNull(creditorDTO.getAddress())) {
                creditor.setAddress(creditorDTO.getAddress());
            }
            if (Objects.nonNull(creditorDTO.getPhoneNumber())) {
                creditor.setPhoneNumber(creditorDTO.getPhoneNumber());
            }
            if (Objects.nonNull(creditorDTO.getEmail())) {
                creditor.setEmail(creditorDTO.getEmail());
            }
            if (Objects.nonNull(creditorDTO.getAccountNumber())) {
                creditor.setAccountNumber(creditorDTO.getAccountNumber());
            }

            return creditorRepository.save(creditor);
        }

        throw new EntityNotFoundException("Creditor not found with id " + id);
    }
    @Override
    public Creditor createCreditor(CreditorDTO creditorDTO) {
        Creditor creditor = new Creditor();
        creditor.setName(creditorDTO.getName());
        creditor.setAddress(creditorDTO.getAddress());
        creditor.setPhoneNumber(creditorDTO.getPhoneNumber());

        Role role = roleRepository.findById(3).orElseThrow(() -> new EntityNotFoundException("Role not found with id 3"));
        CustomUser customUser = new CustomUser();
        customUser.setUsername(creditorDTO.getUserDTO().getUsername());
        customUser.setPassword(bCryptPasswordEncoder.encode(creditorDTO.getUserDTO().getPassword()));
        customUser.setRole(role);
        customUserRepository.save(customUser);

        creditor.setUser(customUser);

        return creditorRepository.save(creditor);
    }
    @Override
    public boolean deleteCreditorById(int id) {
        Optional<Creditor> optionalCreditor = creditorRepository.findById(id);
        if (optionalCreditor.isPresent()) {
            List<DebtCase> debtCases = debtCaseRepository.findAll()
                    .stream()
                    .filter(debtCase -> debtCase.getCreditor().getId() == id).toList();

            if (debtCases.isEmpty()) {
                return false;
            }

            creditorRepository.deleteById(id);

            return true;
        }

        throw new EntityNotFoundException("Creditor not found with id " + id);
    }
}
