package com.dm.debtease.service.impl;

import com.dm.debtease.model.CustomUser;
import com.dm.debtease.model.Role;
import com.dm.debtease.model.dto.UserDTO;
import com.dm.debtease.repository.CustomUserRepository;
import com.dm.debtease.service.CustomUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserServiceImpl implements CustomUserService {
    private final CustomUserRepository customUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public CustomUser createCustomUser(UserDTO userDTO, Role role) {
        CustomUser customUser = new CustomUser();
        customUser.setUsername(userDTO.getUsername());
        customUser.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        customUser.setRole(role);

        return customUserRepository.save(customUser);
    }
}
