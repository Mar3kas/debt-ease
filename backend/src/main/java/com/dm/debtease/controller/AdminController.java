package com.dm.debtease.controller;

import com.dm.debtease.model.Admin;
import com.dm.debtease.service.AdminService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Validated
@CrossOrigin
@RequiredArgsConstructor
@SecurityRequirement(name = "dmapi")
@RequestMapping(value = "/api/admins")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/{username}")
    public ResponseEntity<Object> getUserByUsername(@Valid
                                                    @NotBlank
                                                    @PathVariable(name = "username") String username) {
        Admin admin = adminService.getAdminByUsername(username);
        return ResponseEntity.ok(admin);
    }
}
