package com.dm.debtease.service;

import com.dm.debtease.TestUtils;
import com.dm.debtease.exception.InvalidRefreshTokenException;
import com.dm.debtease.exception.TokenRefreshException;
import com.dm.debtease.model.RefreshToken;
import com.dm.debtease.repository.RefreshTokenRepository;
import com.dm.debtease.service.impl.RefreshTokenServiceImpl;
import com.dm.debtease.utils.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    @Test
    void testCreateRefreshToken() {
        final String id = "493410b3-dd0b-4b78-97bf-289f50f6e74f";
        UUID uuid = UUID.fromString(id);
        mockStatic(UUID.class);
        when(UUID.randomUUID()).thenReturn(uuid);
        String username = "testUser";
        int refreshTokenId = 1;
        RefreshToken expectedRefreshToken = TestUtils.setupRefreshTokenTestData(refreshTokenId, username, id, null);
        when(refreshTokenRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(expectedRefreshToken);
        String actualGeneratedToken = refreshTokenService.createRefreshToken(username);
        Assertions.assertNotNull(actualGeneratedToken);
        Assertions.assertEquals(id, actualGeneratedToken);
    }

    @Test
    void testValidateRefreshToken_ValidToken() {
        RefreshToken validToken = new RefreshToken();
        validToken.setExpirationDate(Instant.now().plusMillis(1000));
        Assertions.assertTrue(refreshTokenService.validateRefreshToken(validToken));
    }

    @Test
    void testValidateRefreshToken_ExpiredToken() {
        RefreshToken expiredToken = new RefreshToken();
        expiredToken.setExpirationDate(Instant.now().minusMillis(1000));
        TokenRefreshException exception = Assertions.assertThrows(TokenRefreshException.class,
                () -> refreshTokenService.validateRefreshToken(expiredToken));
        Assertions.assertEquals(
                String.format("Failed for [%s]: %s", expiredToken.getToken(), Constants.REFRESH_TOKEN_EXPIRED),
                exception.getMessage());
    }

    @Test
    void testFindByValidToken() {
        String username = "testUser";
        String token = UUID.randomUUID().toString();
        int id = 1;
        RefreshToken expectedRefreshToken = TestUtils.setupRefreshTokenTestData(id, username, token, null);
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(expectedRefreshToken));
        RefreshToken actualRefreshToken = refreshTokenService.findByToken(token);
        Assertions.assertNotNull(actualRefreshToken);
        Assertions.assertEquals(expectedRefreshToken.getToken(), actualRefreshToken.getToken());
        Assertions.assertEquals(expectedRefreshToken.getUsername(), actualRefreshToken.getUsername());
    }

    @Test
    void testFindByInvalidToken() {
        String token = UUID.randomUUID().toString();
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.empty());
        InvalidRefreshTokenException exception = Assertions.assertThrows(InvalidRefreshTokenException.class,
                () -> refreshTokenService.findByToken(token));
        Assertions.assertEquals(String.format(Constants.REFRESH_TOKEN_NOT_FOUND, token), exception.getMessage());
    }
}