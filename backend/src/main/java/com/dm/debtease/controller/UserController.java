package com.dm.debtease.controller;

import com.dm.debtease.config.JwtTokenProvider;
import com.dm.debtease.exception.InvalidRefreshTokenException;
import com.dm.debtease.exception.InvalidTokenException;
import com.dm.debtease.exception.JwtTokenCreationException;
import com.dm.debtease.exception.LogoutException;
import com.dm.debtease.model.RefreshToken;
import com.dm.debtease.model.RefreshTokenRequest;
import com.dm.debtease.model.dto.UserDTO;
import com.dm.debtease.repository.RefreshTokenRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Validated
@CrossOrigin
@RequestMapping(value = "/api/users")
@SecurityRequirement(name = "dmapi")
public class UserController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserDetailsService userDetailsService;

    @Autowired
    public UserController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
                          RefreshTokenRepository refreshTokenRepository, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> authenticateUser(@RequestBody @Valid UserDTO userDTO, BindingResult result) throws JwtTokenCreationException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtTokenProvider.createToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(userDTO.getUsername());

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", refreshToken);

        return ResponseEntity.ok(tokenMap);
    }

    @PostMapping("/refresh/token")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestBody @Valid RefreshTokenRequest request,
                                                                  BindingResult result) throws InvalidRefreshTokenException, JwtTokenCreationException {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidRefreshTokenException(String.format("Refresh token not found by this token %s", request.getRefreshToken())));

        if (jwtTokenProvider.validateRefreshToken(refreshToken)) {
            String token = refreshToken.getToken();
            UserDetails user = userDetailsService.loadUserByUsername(refreshToken.getUsername());

            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);

            jwtTokenProvider.deleteRefreshToken(token);
            String accessToken = jwtTokenProvider.createToken(authentication);

            Map<String, String> tokenMap = new HashMap<>();
            tokenMap.put("accessToken", accessToken);

            return ResponseEntity.ok(tokenMap);
        }

        throw new InvalidRefreshTokenException("Error with refresh token");
    }

    @PostMapping("/logout")
    @Transactional
    public ResponseEntity<String> logout(HttpServletRequest request) throws LogoutException {
        try {
            String accessToken = jwtTokenProvider.resolveToken(request);

            if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
                String username = jwtTokenProvider.getUsername(accessToken);
                refreshTokenRepository.deleteByUsername(username);
                SecurityContextHolder.clearContext();

                jwtTokenProvider.addToRevokedTokens(accessToken);

                return ResponseEntity.ok("Logout successful");
            }

            throw new InvalidTokenException("Invalid access token");
        } catch (Exception e) {
            throw new LogoutException("Error during logout");
        }
    }
}
