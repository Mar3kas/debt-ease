package com.dm.debtease.utils;

import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class Constants {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATE_TIME_FORMATTER_FOR_FILE = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final String DEBT_CASE_NOT_FOUND = "Debt case not found with id %s";
    public static final String DEBT_CASE_NOT_FOUND_WITH_ID_CREDITOR_ID =
            "Debt case not found with id %s or creditor id %s";
    public static final String DEBT_CASE_TYPE_NOT_FOUND = "Debt case type not found with id %s";
    public static final String DEBT_CASE_STATUS_NOT_FOUND = "Debt case status not found with id %s";
    public static final String DEBT_CASES_EMPTY = "User %s has no active debt cases";
    public static final String NOT_CSV = "Uploaded file is not a CSV file";
    public static final String CREDITOR_NOT_FOUND = "Creditor not found with id %s";
    public static final String DEBTOR_NOT_FOUND = "Debtor not found with id %s";
    public static final String ROLE_NOT_FOUND = "ROLE not found with id %s";
    public static final String USER_NOT_FOUND = "User not found with username %s";
    public static final String REFRESH_TOKEN_EXPIRED = "Refresh token is expired. Please make a new login request";
    public static final String REFRESH_TOKEN_NOT_FOUND = "Refresh token not found by this token %s";
    public static final String GENERATED_PDF_INTRO_MESSAGE =
            "We hope this message finds you well. Below is a summary of all your active debt cases.";
    public static final String GENERATED_PDF_GREETING_MESSAGE = "Dear %s %s,";
    public static final String GENERATED_PDF_TITLE = "Debt Cases Report";
    public static final String GENERATED_PDF_DISCLAIMER =
            "Disclaimer: Please scroll down to see the debt case type distribution.";
    public static final long DELAY_BETWEEN_NUMVERIFY_REQUESTS = 1000;
}
