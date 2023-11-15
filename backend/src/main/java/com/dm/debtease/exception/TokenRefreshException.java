package com.dm.debtease.exception;

import org.springframework.security.core.AuthenticationException;

public class TokenRefreshException extends AuthenticationException {
    public TokenRefreshException(String token, String message) {
        super(String.format("Failed for [%s]: %s", token, message));
    }
}
