package com.dm.debtease.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface JwtService {
    String createToken(Authentication authentication);

    String resolveToken(HttpServletRequest request);

    String getUsername(String token);

    boolean validateToken(String token);

    Claims parseClaims(String token);

    void addToRevokedTokens(String token);

    boolean isTokenRevoked(String token);
}
