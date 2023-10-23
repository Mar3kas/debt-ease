package com.dm.debtease.config;

import com.dm.debtease.exception.TokenRefreshException;
import com.dm.debtease.model.RefreshToken;
import com.dm.debtease.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
@Log4j2
public class JwtTokenProvider {
    private final Key key;
    private final RefreshTokenRepository refreshTokenRepository;
    private final Set<String> revokedTokens = new HashSet<>();
    @Value("${spring.jwt.accessTokenExpirationInMs}")
    private long accessTokenExpiration;
    @Value("${spring.jwt.refreshTokenExpirationInMs}")
    private long refreshTokenExpiration;

    public JwtTokenProvider(RefreshTokenRepository refreshTokenRepository) {
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String createToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();
        String role = roles.iterator().next().getAuthority();

        Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
        claims.put("role", role);
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    public String createRefreshToken(String username) {
        RefreshToken refreshToken = new RefreshToken();
        String token = UUID.randomUUID().toString();

        refreshToken.setToken(token);
        refreshToken.setExpirationDate(Instant.now().plusMillis(refreshTokenExpiration));
        refreshToken.setUsername(username);

        refreshTokenRepository.save(refreshToken);

        return token;
    }

    public String getUsername(String token) {
        Claims claims = parseClaims(token);

        return claims != null ? claims.getSubject() : null;
    }

    public boolean validateToken(String token) {
        Claims claims = parseClaims(token);

        return claims != null;
    }

    public boolean validateRefreshToken(RefreshToken token) {
        if (token.getExpirationDate().compareTo(Instant.now()) < 0) {
            throw new TokenRefreshException(token.getToken(), "Refresh token is expired. Please make a new login request");
        }

        return true;
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public void addToRevokedTokens(String token) {
        revokedTokens.add(token);
    }

    public boolean isTokenRevoked(String token) {
        return revokedTokens.contains(token);
    }
}