package com.dm.debtease.model.dto;

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
            regexp = "^(\\+\\d{1,2}[-\\s]?)?\\(?\\d{2,3}\\)?[-\\s]?\\d{3}[-\\s]?\\d{4}$",
            message = "Phone number should be a valid format"
    )
    @NotBlank(message = "Phone number is required")
    String phoneNumber;

    @Email
    @NotBlank(message = "Email is required")
    String email;

    @NotBlank(message = "Account number is required")
    String accountNumber;

    String username;
}
