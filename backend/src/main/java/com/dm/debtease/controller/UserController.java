package com.dm.debtease.controller;

import com.dm.debtease.exception.InvalidRefreshTokenException;
import com.dm.debtease.exception.LoginException;
import com.dm.debtease.exception.LogoutException;
import com.dm.debtease.model.*;
import com.dm.debtease.model.dto.CustomUserDTO;
import com.dm.debtease.service.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Validated
@CrossOrigin
@RequiredArgsConstructor
@SecurityRequirement(name = "dmapi")
@RequestMapping(value = "/api")
@SuppressWarnings("unused")
public class UserController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;
    private final CreditorService creditorService;
    private final DebtorService debtorService;
    private final AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> authenticateUser(@RequestBody @Valid CustomUserDTO userDTO,
                                                                BindingResult result) {
        Authentication existingAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (existingAuthentication != null && existingAuthentication.getName().equals(userDTO.getUsername())) {
            throw new LoginException("User is already logged in");
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtService.createToken(authentication);
        String refreshToken = refreshTokenService.createRefreshToken(userDTO.getUsername());
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", refreshToken);
        return ResponseEntity.ok(tokenMap);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestBody @Valid RefreshTokenRequest request,
                                                                  BindingResult result) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken());
        if (refreshTokenService.validateRefreshToken(refreshToken)) {
            UserDetails user = userDetailsService.loadUserByUsername(refreshToken.getUser().getUsername());
            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);
            String accessToken = jwtService.createToken(authentication);
            Map<String, String> tokenMap = new HashMap<>();
            tokenMap.put("accessToken", accessToken);
            return ResponseEntity.ok(tokenMap);
        }
        throw new InvalidRefreshTokenException("Error with refresh token");
    }

    @PostMapping("/logout")
    @Transactional
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        String accessToken = jwtService.resolveToken(request);
        if (accessToken != null && !jwtService.isTokenRevoked(accessToken)) {
            SecurityContextHolder.clearContext();
            jwtService.addToRevokedTokens(accessToken);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Logout successful");
            return ResponseEntity.ok(response);
        }
        throw new LogoutException("Error during logout");
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<Object> getUserByUsername(@Valid
                                                    @NotBlank
                                                    @PathVariable(name = "username") String username) {
        Creditor creditor = creditorService.getCreditorByUsername(username);
        if (creditor != null) {
            return ResponseEntity.ok(creditor);
        }
        Debtor debtor = debtorService.getDebtorByUsername(username);
        if (debtor != null) {
            return ResponseEntity.ok(debtor);
        }
        Admin admin = adminService.getAdminByUsername(username);
        if (admin != null) {
            return ResponseEntity.ok(admin);
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
