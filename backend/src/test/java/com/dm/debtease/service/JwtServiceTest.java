package com.dm.debtease.service;

import com.dm.debtease.service.impl.JwtServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashSet;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private JwtServiceImpl jwtService;

    @Test
    void createToken_WithUserDetails_ShouldGenerateToken() {
        HashSet<GrantedAuthority> roles = new HashSet<>();
        roles.add(new SimpleGrantedAuthority("ADMIN"));
        UserDetails userDetails = new User("testUser", "password", roles);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String token = jwtService.createToken(authentication);

        Assertions.assertNotNull(token);
    }

    @Test
    void resolveToken_WithValidTokenInRequestHeader_ShouldResolveToken() {
        String testToken = "Bearer testToken";
        when(request.getHeader("Authorization")).thenReturn(testToken);

        String resolvedToken = jwtService.resolveToken(request);

        Assertions.assertEquals("testToken", resolvedToken);
    }

    @Test
    void resolveToken_WithInvalidTokenInRequestHeader_ShouldReturnNull() {
        String testToken = "Test testToken";
        when(request.getHeader("Authorization")).thenReturn(testToken);

        String resolvedToken = jwtService.resolveToken(request);

        Assertions.assertNull(resolvedToken);
    }

    @Test
    void getUsername_WithValidToken_ShouldExtractUsername() {
        String token = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(jwtService.getKey())
                .compact();

        String username = jwtService.getUsername(token);

        Assertions.assertEquals("testUser", username);
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        String token = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(jwtService.getKey())
                .compact();

        boolean isValid = jwtService.validateToken(token);

        Assertions.assertTrue(isValid);
    }

    @Test
    void parseClaims_WithValidToken_ShouldParseClaims() {
        String token = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(jwtService.getKey())
                .compact();

        Claims claims = jwtService.parseClaims(token);

        Assertions.assertNotNull(claims);
        Assertions.assertEquals("testUser", claims.getSubject());
    }

    @Test
    void addToRevokedTokens_WithValidToken_ShouldAddToRevokedTokens() {
        String token = "testToken";

        jwtService.addToRevokedTokens(token);

        Assertions.assertTrue(jwtService.isTokenRevoked(token));
    }

    @Test
    void isTokenRevoked_WithRevokedToken_ShouldReturnTrue() {
        String token = "testToken";

        jwtService.addToRevokedTokens(token);
        boolean isRevoked = jwtService.isTokenRevoked(token);

        Assertions.assertTrue(isRevoked);
    }
}
