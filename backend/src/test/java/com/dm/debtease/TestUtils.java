package com.dm.debtease;

import com.dm.debtease.model.*;
import com.dm.debtease.model.dto.CreditorDTO;
import com.dm.debtease.model.dto.DebtCaseDTO;
import com.dm.debtease.model.dto.DebtorDTO;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public class TestUtils {
    public final static String[] INDICATOR = new String[]{
            "84.35",
            "2024-12-12 23:00:00",
            "TAX_DEBT",
            "Tadas",
            "Tadaitis"
    };

    public static Admin setupAdminTestData(String username) {
        Admin admin = new Admin();
        CustomUser user = new CustomUser();
        user.setUsername(username);
        admin.setUser(user);
        return admin;
    }

    public static Creditor setupCreditorTestData(String username, int id) {
        Creditor creditor = new Creditor();
        CustomUser user = new CustomUser();
        user.setUsername(username);
        creditor.setId(id);
        creditor.setUser(user);
        creditor.setName("testName");
        creditor.setEmail("testemail@gmail.com");
        return creditor;
    }

    public static Creditor setupCreditorTestData(String name, String email, String address, String phoneNumber,
                                                       String accountNumber, String username) {
        Creditor creditor = new Creditor();
        creditor.setId(1);
        creditor.setName(name);
        creditor.setEmail(email);
        creditor.setAddress(address);
        creditor.setPhoneNumber(phoneNumber);
        creditor.setAccountNumber(accountNumber);
        creditor.setUser(setupCustomUserTestData(username, "password", Role.CREDITOR));
        return creditor;
    }

    public static CreditorDTO setupCreditorDTOTestData(String name, String email, String address, String phoneNumber,
                                                       String accountNumber) {
        CreditorDTO creditorDTO = new CreditorDTO();
        creditorDTO.setName(name);
        creditorDTO.setUsername(name);
        creditorDTO.setEmail(email);
        creditorDTO.setAddress(address);
        creditorDTO.setPhoneNumber(phoneNumber);
        creditorDTO.setAccountNumber(accountNumber);
        return creditorDTO;
    }

    public static Debtor setupDebtorTestData(String name, String surname, String email, String phoneNumber,
                                             String username) {
        Debtor debtor = new Debtor();
        CustomUser user = new CustomUser();
        debtor.setId(1);
        user.setUsername(username);
        debtor.setName(name);
        debtor.setSurname(surname);
        debtor.setEmail(email);
        debtor.setPhoneNumber(phoneNumber);
        debtor.setUser(user);
        return debtor;
    }

    public static Debtor setupEditedDebtorTestData(String name, String surname, String email, String phoneNumber) {
        Debtor debtor = new Debtor();
        debtor.setName(name);
        debtor.setSurname(surname);
        debtor.setEmail(email);
        debtor.setPhoneNumber(phoneNumber);
        return debtor;
    }

    public static DebtorDTO setupDebtorDTOTestData(String name, String surname, String email, String phoneNumber) {
        DebtorDTO debtorDTO = new DebtorDTO();
        debtorDTO.setName(name);
        debtorDTO.setSurname(surname);
        debtorDTO.setEmail(email);
        debtorDTO.setPhoneNumber(phoneNumber);
        return debtorDTO;
    }

    public static CustomUser setupCustomUserTestData(String username, String password, Role role) {
        CustomUser customUser = new CustomUser();
        customUser.setUsername(username);
        customUser.setPassword(password);
        customUser.setRole(role);
        return customUser;
    }

    public static DebtCaseType setupDebtCaseTypeTestData(String type) {
        DebtCaseType debtCaseType = new DebtCaseType();
        debtCaseType.setId(15);
        debtCaseType.setType(type);
        return debtCaseType;
    }

    public static RefreshToken setupRefreshTokenTestData(int id, String username, String token,
                                                         Instant expirationDate) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(id);
        refreshToken.setUsername(username);
        refreshToken.setToken(token);
        refreshToken.setExpirationDate(expirationDate);
        return refreshToken;
    }

    public static DebtCase setupDebtCaseTestData(String creditorUsername, int creditorId, String debtorName,
                                                 String debtorSurname,
                                                 String debtorEmail, String debtorPhoneNumber, String type,
                                                 DebtCaseStatus status,
                                                 LocalDateTime dueDate, double lateInterestRate,
                                                 BigDecimal amountOwed, String debtorUsername) {
        DebtCase debtCase = new DebtCase();
        debtCase.setId(1);
        debtCase.setCreditor(setupCreditorTestData(creditorUsername, creditorId
        ));
        debtCase.setDebtor(
                setupDebtorTestData(debtorName, debtorSurname, debtorEmail, debtorPhoneNumber, debtorUsername));
        debtCase.setDebtCaseType(setupDebtCaseTypeTestData(type));
        debtCase.setDebtCaseStatus(status);
        debtCase.setOutstandingBalance(BigDecimal.TEN);
        debtCase.setDueDate(dueDate);
        debtCase.setLateInterestRate(lateInterestRate);
        debtCase.setAmountOwed(amountOwed);
        return debtCase;
    }

    public static DebtCaseDTO setupDebtCaseDTOTestData(BigDecimal amountOwed, LocalDateTime dueDate, int typeId) {
        DebtCaseDTO debtCaseDTO = new DebtCaseDTO();
        debtCaseDTO.setAmountOwed(amountOwed);
        debtCaseDTO.setDueDate(dueDate);
        debtCaseDTO.setTypeId(typeId);
        return debtCaseDTO;
    }
}
