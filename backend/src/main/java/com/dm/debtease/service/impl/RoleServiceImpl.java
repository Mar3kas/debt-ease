package com.dm.debtease.service.impl;

import com.dm.debtease.model.Role;
import com.dm.debtease.repository.RoleRepository;
import com.dm.debtease.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public Role getRoleById(int id) {
        return roleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Role not found with id " + id));
    }
}
