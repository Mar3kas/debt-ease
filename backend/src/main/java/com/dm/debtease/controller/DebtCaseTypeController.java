package com.dm.debtease.controller;

import com.dm.debtease.model.DebtCaseType;
import com.dm.debtease.service.DebtCaseTypeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@CrossOrigin
@RequiredArgsConstructor
@SecurityRequirement(name = "dmapi")
@RequestMapping(value = "/api/debtcase/types")
@SuppressWarnings("unused")
public class DebtCaseTypeController {
    private final DebtCaseTypeService debtCaseTypeService;

    @GetMapping()
    public ResponseEntity<List<DebtCaseType>> getAllDebtCaseTypes() {
        List<DebtCaseType> debtCaseTypes = debtCaseTypeService.getAllDebtCaseTypes();
        return ResponseEntity.ok(debtCaseTypes);
    }
}
