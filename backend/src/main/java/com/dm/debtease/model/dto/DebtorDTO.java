package com.dm.debtease.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DebtorDTO {
    @NotBlank(message = "Name is required")
    String name;

    @NotBlank(message = "Surname is required")
    String surname;

    @Email
    @NotBlank(message = "Email is required")
    String email;

    @Pattern(
            regexp = "^(\\+\\d{1,2}[-\\s]?)?\\(?\\d{2,3}\\)?[-\\s]?\\d{3}[-\\s]?\\d{4}$",
            message = "Phone number should be a valid format"
    )
    @NotBlank(message = "Phone number is required")
    String phoneNumber;

    String username = "";
}
