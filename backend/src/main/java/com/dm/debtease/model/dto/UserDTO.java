package com.dm.debtease.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    @NotBlank(message = "Username is required")
    String username;
    @NotBlank(message = "Password is required")
    String password;
}
