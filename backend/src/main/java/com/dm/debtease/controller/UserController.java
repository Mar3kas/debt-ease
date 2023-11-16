package com.dm.debtease.controller;

import com.dm.debtease.exception.InvalidRefreshTokenException;
import com.dm.debtease.exception.InvalidTokenException;
import com.dm.debtease.exception.LoginException;
import com.dm.debtease.exception.LogoutException;
import com.dm.debtease.model.Admin;
import com.dm.debtease.model.Creditor;
import com.dm.debtease.model.Debtor;
import com.dm.debtease.model.RefreshToken;
import com.dm.debtease.model.RefreshTokenRequest;
import com.dm.debtease.model.dto.UserDTO;
import com.dm.debtease.service.AdminService;
import com.dm.debtease.service.CreditorService;
import com.dm.debtease.service.DebtorService;
import com.dm.debtease.service.JwtService;
import com.dm.debtease.service.RefreshTokenService;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@Validated
@CrossOrigin
@RequiredArgsConstructor
@SecurityRequirement(name = "dmapi")
@RequestMapping(value = "/api")
public class UserController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;
    private final CreditorService creditorService;
    private final DebtorService debtorService;
    private final AdminService adminService;

    @GetMapping("/users/{username}")
    public ResponseEntity<Object> getUserByUsername(@Valid
                                                    @NotBlank
                                                    @PathVariable(name = "username") String username) {
        Creditor creditor = creditorService.getCreditorByUsername(username);
        Debtor debtor = debtorService.getDebtorByUsername(username);
        Admin admin = adminService.getAdminByUsername(username);

        if (Objects.nonNull(creditor)) {
            return ResponseEntity.ok(creditor);
        } else if (Objects.nonNull(debtor)) {
            return ResponseEntity.ok(debtor);
        } else if (Objects.nonNull(admin)) {
            return ResponseEntity.ok(admin);
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> authenticateUser(@RequestBody @Valid UserDTO userDTO, BindingResult result) {
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
            UserDetails user = userDetailsService.loadUserByUsername(refreshToken.getUsername());

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
    public ResponseEntity<String> logout(HttpServletRequest request) {
        try {
            String accessToken = jwtService.resolveToken(request);

            if (accessToken != null) {
                SecurityContextHolder.clearContext();

                jwtService.addToRevokedTokens(accessToken);

                return ResponseEntity.ok("Logout successful");
            }

            throw new InvalidTokenException("Invalid access token");
        } catch (RuntimeException e) {
            throw new LogoutException("Error during logout");
        }
    }
}
