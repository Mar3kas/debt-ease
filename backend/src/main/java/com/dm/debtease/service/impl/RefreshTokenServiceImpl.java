package com.dm.debtease.service.impl;

import com.dm.debtease.exception.InvalidRefreshTokenException;
import com.dm.debtease.exception.TokenRefreshException;
import com.dm.debtease.model.CustomUser;
import com.dm.debtease.model.RefreshToken;
import com.dm.debtease.repository.CustomUserRepository;
import com.dm.debtease.repository.RefreshTokenRepository;
import com.dm.debtease.service.RefreshTokenService;
import com.dm.debtease.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@SuppressWarnings("unused")
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserRepository customUserRepository;
    @Value("${spring.jwt.refreshTokenExpirationInMs}")
    private long refreshTokenExpiration;

    @Override
    public String createRefreshToken(String username) {
        Optional<CustomUser> optionalCustomUser = customUserRepository.findByUsername(username);
        if (optionalCustomUser.isPresent())
        {
            CustomUser customUser = optionalCustomUser.get();
            Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByCustomUser_Username(customUser.getUsername());
            RefreshToken refreshToken = optionalRefreshToken.orElse(new RefreshToken());
            String token = UUID.randomUUID().toString();
            refreshToken.setToken(token);
            refreshToken.setExpirationDate(Instant.now().plusMillis(refreshTokenExpiration));
            refreshToken.setCustomUser(customUser);
            refreshTokenRepository.save(refreshToken);
            return token;
        }
        throw new UsernameNotFoundException(String.format(Constants.USER_NOT_FOUND, username));
    }

    @Override
    public boolean validateRefreshToken(RefreshToken token) {
        if (token.getExpirationDate().compareTo(Instant.now()) < 0) {
            throw new TokenRefreshException(token.getToken(), Constants.REFRESH_TOKEN_EXPIRED);
        }
        return true;
    }

    @Override
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException(
                        String.format(Constants.REFRESH_TOKEN_NOT_FOUND, token)));
    }
}
