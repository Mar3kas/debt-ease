package com.dm.debtease.service;

import com.dm.debtease.TestUtils;
import com.dm.debtease.exception.InvalidRefreshTokenException;
import com.dm.debtease.exception.TokenRefreshException;
import com.dm.debtease.model.CustomUser;
import com.dm.debtease.model.RefreshToken;
import com.dm.debtease.model.Role;
import com.dm.debtease.repository.CustomUserRepository;
import com.dm.debtease.repository.RefreshTokenRepository;
import com.dm.debtease.service.impl.RefreshTokenServiceImpl;
import com.dm.debtease.utils.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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
    @Mock
    private CustomUserRepository customUserRepository;
    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    @Test
    void createRefreshToken_ShouldGenerateUniqueToken() {
        final String id = "493410b3-dd0b-4b78-97bf-289f50f6e74f";
        UUID uuid = UUID.fromString(id);
        mockStatic(UUID.class);
        when(UUID.randomUUID()).thenReturn(uuid);
        String username = "testUser";
        int refreshTokenId = 1;
        String password = "testPassword";
        CustomUser customUser = TestUtils.setupCustomUserTestData(username, password, Role.ADMIN);
        RefreshToken expectedRefreshToken = TestUtils.setupRefreshTokenTestData(refreshTokenId, id, null);
        expectedRefreshToken.setUser(customUser);
        when(customUserRepository.findByUsername(username)).thenReturn(Optional.of(customUser));
        when(refreshTokenRepository.findByUser_Username(username)).thenReturn(Optional.empty());
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(expectedRefreshToken);

        String actualGeneratedToken = refreshTokenService.createRefreshToken(username);

        Assertions.assertNotNull(actualGeneratedToken);
        Assertions.assertEquals(id, actualGeneratedToken);
    }

    @Test
    void createRefreshToken_InvalidUsername_ShouldThrowException() {
        String invalidUsername = "invalid";
        when(customUserRepository.findByUsername(invalidUsername)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = Assertions.assertThrows(UsernameNotFoundException.class,
                () -> refreshTokenService.createRefreshToken(invalidUsername));

        Assertions.assertEquals(String.format(Constants.USER_NOT_FOUND, invalidUsername), exception.getMessage());
    }

    @Test
    void validateRefreshToken_WithValidToken_ShouldReturnTrue() {
        RefreshToken validToken = new RefreshToken();
        validToken.setExpirationDate(Instant.now().plusMillis(1000));

        Assertions.assertTrue(refreshTokenService.validateRefreshToken(validToken));
    }

    @Test
    void validateRefreshToken_WithExpiredToken_ShouldThrowException() {
        RefreshToken expiredToken = new RefreshToken();
        expiredToken.setExpirationDate(Instant.now().minusMillis(1000));

        TokenRefreshException exception = Assertions.assertThrows(TokenRefreshException.class,
                () -> refreshTokenService.validateRefreshToken(expiredToken));

        Assertions.assertEquals(
                String.format("Failed for [%s]: %s", expiredToken.getToken(), Constants.REFRESH_TOKEN_EXPIRED),
                exception.getMessage());
    }

    @Test
    void findByToken_WithValidToken_ShouldReturnRefreshToken() {
        String username = "testUser";
        String password = "testPassword";
        String token = UUID.randomUUID().toString();
        int id = 1;
        CustomUser customUser = TestUtils.setupCustomUserTestData(username, password, Role.ADMIN);
        RefreshToken expectedRefreshToken = TestUtils.setupRefreshTokenTestData(id, token, null);
        expectedRefreshToken.setUser(customUser);
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(expectedRefreshToken));

        RefreshToken actualRefreshToken = refreshTokenService.findByToken(token);

        Assertions.assertNotNull(actualRefreshToken);
        Assertions.assertEquals(expectedRefreshToken.getToken(), actualRefreshToken.getToken());
        Assertions.assertEquals(expectedRefreshToken.getUser().getUsername(), actualRefreshToken.getUser().getUsername());
    }

    @Test
    void findByToken_WithInvalidToken_ShouldThrowException() {
        String token = UUID.randomUUID().toString();
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        InvalidRefreshTokenException exception = Assertions.assertThrows(InvalidRefreshTokenException.class,
                () -> refreshTokenService.findByToken(token));

        Assertions.assertEquals(String.format(Constants.REFRESH_TOKEN_NOT_FOUND, token), exception.getMessage());
    }
}