package com.dm.debtease.config.security;

import com.dm.debtease.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@Nullable HttpServletRequest request, @Nullable HttpServletResponse response,
                                    @Nullable FilterChain filterChain) throws ServletException, IOException {
        if (filterChain != null && request != null) {
            String requestUri = request.getRequestURI();
            if (isRefreshTokenRequest(requestUri)) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = jwtService.resolveToken(request);
            if (token != null) {
                try {
                    if (jwtService.validateToken(token) && !jwtService.isTokenRevoked(token)) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(jwtService.getUsername(token));
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null,
                                        userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                } catch (ExpiredJwtException | UnsupportedJwtException
                         | SignatureException | IllegalStateException | NullPointerException ex) {
                    SecurityContextHolder.clearContext();
                }
            }
            filterChain.doFilter(request, response);
        }
    }

    private boolean isRefreshTokenRequest(String requestUri) {
        return requestUri.endsWith("/refresh");
    }
}
