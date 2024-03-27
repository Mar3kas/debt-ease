package com.dm.debtease.service;

import com.dm.debtease.service.impl.PasswordGeneratorServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PasswordGeneratorServiceTest {
    @InjectMocks
    private PasswordGeneratorServiceImpl passwordGeneratorService;

    @Test
    void generatePassword_LengthZero() {
        String password = passwordGeneratorService.generatePassword(0);

        Assertions.assertEquals("", password, "Password should be empty for length 0");
    }

    @Test
    void generatePassword_PositiveLength() {
        int length = 8;

        String password = passwordGeneratorService.generatePassword(length);

        Assertions.assertEquals(length, password.length(), "Password length should match requested length");
    }

    @Test
    void generatePassword_NegativeLength() {
        int length = -5;

        String password = passwordGeneratorService.generatePassword(length);

        Assertions.assertEquals("", password, "Password should be empty for negative length");
    }

    @Test
    void generatePassword_ContainsLowercase() {
        int length = 10;

        String password = passwordGeneratorService.generatePassword(length);

        Assertions.assertTrue(password.matches(".*[a-z].*"), "Password should contain lowercase characters");
    }

    @Test
    void generatePassword_ContainsUppercase() {
        int length = 10;

        String password = passwordGeneratorService.generatePassword(length);

        Assertions.assertTrue(password.matches(".*[A-Z].*"), "Password should contain uppercase characters");
    }

    @Test
    void generatePassword_ContainsDigits() {
        int length = 10;

        String password = passwordGeneratorService.generatePassword(length);

        Assertions.assertTrue(password.matches(".*[0-9].*"), "Password should contain digits");
    }
}
