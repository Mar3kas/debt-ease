package com.dm.debtease.service.impl;

import com.dm.debtease.model.Admin;
import com.dm.debtease.repository.AdminRepository;
import com.dm.debtease.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;

    @Override
    public Admin getAdminByUsername(String username) {
        Optional<Admin> optionalAdmin = adminRepository.findByUserUsername(username);
        return optionalAdmin.orElse(null);
    }
}
