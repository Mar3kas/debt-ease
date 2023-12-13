package com.dm.debtease.service;

import com.dm.debtease.model.CustomUser;
import com.dm.debtease.model.Role;
import com.dm.debtease.model.dto.UserDTO;

public interface CustomUserService {
    CustomUser createCustomUser(UserDTO userDTO, Role role);
}
