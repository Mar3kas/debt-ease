package com.dm.debtease.service.impl;

import com.dm.debtease.exception.InvalidRefreshTokenException;
import com.dm.debtease.exception.TokenRefreshException;
import com.dm.debtease.model.RefreshToken;
import com.dm.debtease.repository.RefreshTokenRepository;
import com.dm.debtease.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${spring.jwt.refreshTokenExpirationInMs}")
    private long refreshTokenExpiration;

    @Override
    public String createRefreshToken(String username) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByUsername(username);
        RefreshToken refreshToken = optionalRefreshToken.orElse(new RefreshToken());

        String token = UUID.randomUUID().toString();

        refreshToken.setToken(token);
        refreshToken.setExpirationDate(Instant.now().plusMillis(refreshTokenExpiration));
        refreshToken.setUsername(username);

        refreshTokenRepository.save(refreshToken);

        return token;
    }

    @Override
    public boolean validateRefreshToken(RefreshToken token) {
        if (token.getExpirationDate().compareTo(Instant.now()) < 0) {
            throw new TokenRefreshException(token.getToken(), "Refresh token is expired. Please make a new login request");
        }

        return true;
    }

    @Override
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException(String.format("Refresh token not found by this token %s", token)));
    }
}
