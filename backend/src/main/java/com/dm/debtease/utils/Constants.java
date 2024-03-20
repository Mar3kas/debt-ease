package com.dm.debtease.utils;

import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class Constants {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATE_TIME_FORMATTER_FOR_FILE = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final String DEBT_CASE_NOT_FOUND = "Debt case not found with id %s";
    public static final String DEBT_CASE_TYPE_NOT_FOUND = "Debt case type not found with id %s";
    public static final String DEBT_CASE_STATUS_NOT_FOUND = "Debt case status not found with id %s";
    public static final String NOT_CSV = "Uploaded file is not a CSV file";
    public static final String CREDITOR_NOT_FOUND = "Creditor not found with id %s";
    public static final String DEBTOR_NOT_FOUND = "Debtor not found with id %s";
    public static final String ROLE_NOT_FOUND = "ROLE not found with id %s";
    public static final String USER_NOT_FOUND = "User not found with username %s";
    public static final String REFRESH_TOKEN_EXPIRED = "Refresh token is expired. Please make a new login request";
    public static final String REFRESH_TOKEN_NOT_FOUND = "Refresh token not found by this token %s";
    public static final long DELAY_BETWEEN_NUMVERIFY_REQUESTS = 1000;
}
