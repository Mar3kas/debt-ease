package com.dm.debtease.service;

import com.dm.debtease.TestUtils;
import com.dm.debtease.model.Admin;
import com.dm.debtease.repository.AdminRepository;
import com.dm.debtease.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {
    @Mock
    private AdminRepository adminRepository;
    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    void testGetAdminByUsername() {
        String username = "admin123";
        Admin expectedAdmin = TestUtils.setupAdminTestData(username);
        when(adminRepository.findByUserUsername(username)).thenReturn(Optional.of(expectedAdmin));
        Admin actualAdmin = adminService.getAdminByUsername(username);
        Assertions.assertNotNull(actualAdmin);
        Assertions.assertEquals(expectedAdmin.getUser().getUsername(), actualAdmin.getUser().getUsername());
    }

    @Test
    void testGetAdminByInvalidUsername_ShouldReturnEmpty() {
        String username = "nonexistent";
        when(adminRepository.findByUserUsername(username)).thenReturn(Optional.empty());
        Admin actualAdmin = adminService.getAdminByUsername(username);
        Assertions.assertNull(actualAdmin);
    }
}
