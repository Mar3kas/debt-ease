package com.dm.debtease.model.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class CreditorDTO {
    @NotBlank(message = "Name is required")
    String name;
    @NotBlank(message = "Address is required")
    String address;
    @Pattern(
            regexp = "^(\\+\\d{1,2}\\s?)?\\(?\\d{2,3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{3,4}$",
            message = "Phone number should be a valid format"
    )
    String phoneNumber;
    @Email
    String email;
    @NotBlank(message = "Account number is required")
    String accountNumber;
    @Nullable
    UserDTO userDTO;
}
