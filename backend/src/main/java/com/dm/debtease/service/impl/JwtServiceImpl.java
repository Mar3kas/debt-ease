package com.dm.debtease.service.impl;

import com.dm.debtease.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class JwtServiceImpl implements JwtService {
    private final Key key;
    private final Set<String> revokedTokens;
    @Value("${spring.jwt.accessTokenExpirationInMs}")
    private long accessTokenExpiration;

    public JwtServiceImpl() {
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        this.revokedTokens = new HashSet<>();
    }

    @Override
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
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    @Override
    public String getUsername(String token) {
        Claims claims = parseClaims(token);

        return claims != null ? claims.getSubject() : null;
    }

    @Override
    public boolean validateToken(String token) {
        Claims claims = parseClaims(token);

        return claims != null;
    }

    @Override
    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public void addToRevokedTokens(String token) {
        revokedTokens.add(token);
    }

    @Override
    public boolean isTokenRevoked(String token) {
        return revokedTokens.contains(token);
    }
}