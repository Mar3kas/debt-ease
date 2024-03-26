package com.dm.debtease.controller;

import com.dm.debtease.TestUtils;
import com.dm.debtease.model.*;
import com.dm.debtease.model.dto.UserDTO;
import com.dm.debtease.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private CreditorService creditorService;
    @Mock
    private DebtorService debtorService;
    @Mock
    private AdminService adminService;
    @InjectMocks
    private UserController userController;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testAuthenticateUserWhenUserIsValidShouldReturnTokens() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("user");
        userDTO.setPassword("password");
        Authentication mockedAuthentication =
                new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword());
        given(authenticationManager.authenticate(any(Authentication.class))).willReturn(mockedAuthentication);
        when(jwtService.createToken(any(Authentication.class))).thenReturn("accessToken");
        given(refreshTokenService.createRefreshToken(any(String.class))).willReturn("refreshToken");
        MvcResult result = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"))
                .andDo(print())
                .andReturn();
        verify(jwtService).createToken(any(Authentication.class));
        verify(refreshTokenService).createRefreshToken(userDTO.getUsername());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType());
        Assertions.assertNotNull(result.getResponse().getContentAsString());
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    void testRefreshAccessToken() throws Exception {
        RefreshTokenRequest mockedRequest = new RefreshTokenRequest();
        mockedRequest.setRefreshToken("valid_refresh_token");
        RefreshToken mockedRefreshToken = new RefreshToken();
        mockedRefreshToken.setUsername("user");
        when(refreshTokenService.findByToken(mockedRequest.getRefreshToken())).thenReturn(mockedRefreshToken);
        when(refreshTokenService.validateRefreshToken(mockedRefreshToken)).thenReturn(true);
        UserDetails userDetails = new User("user", "", new ArrayList<>());
        when(userDetailsService.loadUserByUsername(userDetails.getUsername())).thenReturn(userDetails);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
        String accessToken = "new_access_token";
        when(jwtService.createToken(authentication)).thenReturn(accessToken);
        MvcResult result = mockMvc.perform(post("/api/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mockedRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(accessToken))
                .andDo(print())
                .andReturn();
        verify(refreshTokenService).findByToken(mockedRequest.getRefreshToken());
        verify(refreshTokenService).validateRefreshToken(mockedRefreshToken);
        verify(userDetailsService).loadUserByUsername(userDetails.getUsername());
        verify(jwtService).createToken(authentication);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType());
        Assertions.assertNotNull(result.getResponse().getContentAsString());
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    void testLogout() throws Exception {
        String accessToken = "valid_access_token";
        when(jwtService.resolveToken(any(HttpServletRequest.class))).thenReturn(accessToken);
        when(jwtService.isTokenRevoked(accessToken)).thenReturn(false);
        MvcResult result = mockMvc.perform(post("/api/logout"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Logout successful"))
                .andDo(print())
                .andReturn();
        verify(jwtService).resolveToken(any(HttpServletRequest.class));
        verify(jwtService).isTokenRevoked(accessToken);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType());
        Assertions.assertNotNull(result.getResponse().getContentAsString());
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    public void testGetCreditorUserByUsername() throws Exception {
        String username = "test_creditor";
        int id = 1;
        Creditor mockedCreditor = TestUtils.setupCreditorTestData(username, id);
        when(creditorService.getCreditorByUsername(anyString())).thenReturn(mockedCreditor);
        MvcResult result = mockMvc.perform(get("/api/users/{username}", username))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user.username").value(username))
                .andDo(print())
                .andReturn();
        verify(creditorService).getCreditorByUsername(anyString());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType());
        Assertions.assertNotNull(result.getResponse().getContentAsString());
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    public void testGetDebtorUserByUsername() throws Exception {
        String username = "debtor";
        String name = "name";
        String surname = "surname";
        String email = "email@gmail.com";
        String phoneNumber = "+37067144213";
        Debtor mockedDebtor = TestUtils.setupDebtorTestData(name, surname, email, phoneNumber, username);
        when(debtorService.getDebtorByUsername(anyString())).thenReturn(mockedDebtor);
        MvcResult result = mockMvc.perform(get("/api/users/{username}", username))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user.username").value(username))
                .andDo(print())
                .andReturn();
        verify(debtorService).getDebtorByUsername(anyString());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType());
        Assertions.assertNotNull(result.getResponse().getContentAsString());
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    public void testGetAdminUserByUsername() throws Exception {
        String username = "test_admin";
        Admin mockedAdmin = TestUtils.setupAdminTestData(username);
        when(adminService.getAdminByUsername(anyString())).thenReturn(mockedAdmin);
        MvcResult result = mockMvc.perform(get("/api/users/{username}", username))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user.username").value(username))
                .andDo(print())
                .andReturn();
        verify(adminService).getAdminByUsername(anyString());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType());
        Assertions.assertNotNull(result.getResponse().getContentAsString());
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }
}
