package com.dm.debtease.config;

import com.dm.debtease.model.APIError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String errorMessage = authException.getMessage();

        APIError error = APIError.builder()
                .statusCode(HttpServletResponse.SC_UNAUTHORIZED)
                .time(LocalDateTime.now())
                .message("Unauthorized")
                .description(errorMessage)
                .build();

        objectMapper.writeValue(response.getWriter(), error);
    }
}