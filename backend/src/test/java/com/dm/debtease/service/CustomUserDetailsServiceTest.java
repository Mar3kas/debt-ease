package com.dm.debtease.service;

import com.dm.debtease.TestUtils;
import com.dm.debtease.model.CustomUser;
import com.dm.debtease.repository.CustomUserRepository;
import com.dm.debtease.service.impl.CustomUserDetailsServiceImpl;
import com.dm.debtease.utils.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {
    @Mock
    private CustomUserRepository customUserRepository;
    @InjectMocks
    private CustomUserDetailsServiceImpl userDetailsService;

    @Test
    void testLoadExistingUserByUsername() {
        String username = "testUser";
        String password = "testPassword";
        String roleName = "ROLE_USER";
        int roleId = 3;
        CustomUser customUser = TestUtils.setupCustomUserTestData(username, password, roleName, roleId);
        when(customUserRepository.findByUsername(username)).thenReturn(Optional.of(customUser));
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Assertions.assertNotNull(userDetails);
        Assertions.assertEquals(username, userDetails.getUsername());
        Assertions.assertEquals(password, userDetails.getPassword());
        Assertions.assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(roleName)));
    }

    @Test
    void testLoadNonExistingUserByUsername() {
        String username = "nonExistingUser";
        when(customUserRepository.findByUsername(username)).thenReturn(Optional.empty());
        UsernameNotFoundException thrown = Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(username),
                "Expected loadUserByUsername to throw, but it didn't"
        );
        Assertions.assertTrue(thrown.getMessage().contains(String.format(Constants.USER_NOT_FOUND, username)));
    }
}
