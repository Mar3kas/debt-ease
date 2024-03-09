package com.dm.debtease.controller;

import com.dm.debtease.exception.InvalidFileFormatException;
import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.dto.DebtCaseDTO;
import com.dm.debtease.service.DebtCaseService;
import com.opencsv.exceptions.CsvValidationException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@Validated
@CrossOrigin
@RequiredArgsConstructor
@SecurityRequirement(name = "dmapi")
@RequestMapping(value = "/api/debtcases")
public class DebtCaseController {
    private final DebtCaseService debtCaseService;

    @GetMapping()
    public ResponseEntity<List<DebtCase>> getAllDebtCases() {
        List<DebtCase> debtCases = debtCaseService.getAllDebtCases();
        return ResponseEntity.ok(debtCases);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DebtCase> getDebtCaseById(@Valid
                                                    @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                    @PathVariable(name = "id") int id) {
        DebtCase debtCase = debtCaseService.getDebtCaseById(id);
        return ResponseEntity.ok(debtCase);
    }

    @GetMapping("/creditor/{username}")
    public ResponseEntity<List<DebtCase>> getDebtCasesByCreditorUsername(@Valid
                                                                         @NotBlank
                                                                         @PathVariable(name = "username") String username) {
        List<DebtCase> debtCases = debtCaseService.getDebtCasesByCreditorUsername(username);
        return ResponseEntity.ok(debtCases);
    }

    @GetMapping("/debtor/{username}")
    public ResponseEntity<List<DebtCase>> getDebtCasesByDebtorUsername(@Valid
                                                                       @PathVariable(name = "username") String username) {
        List<DebtCase> debtCases = debtCaseService.getDebtCasesByDebtorUsername(username);
        return ResponseEntity.ok(debtCases);
    }

    @PutMapping("/{id}/creditors/{creditorId}")
    public ResponseEntity<DebtCase> editDebtCaseById(@Valid @RequestBody DebtCaseDTO debtCaseDTO, BindingResult result,
                                                     @Valid
                                                     @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                     @PathVariable(name = "id") int id,
                                                     @Valid
                                                     @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                     @PathVariable(name = "creditorId") int creditorId) {
        DebtCase debtCase = debtCaseService.editDebtCaseById(debtCaseDTO, id, creditorId);
        return ResponseEntity.ok(debtCase);
    }

    @PostMapping(value = "/creditors/{username}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<DebtCase>> createDebtCase(@Valid
                                                         @NotBlank
                                                         @PathVariable(name = "username") String username,
                                                         @RequestParam(name = "file") MultipartFile file) throws CsvValidationException, IOException, InvalidFileFormatException {
        List<DebtCase> debtCases = debtCaseService.createDebtCase(file, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(debtCases);
    }

    @DeleteMapping("/{id}/creditors/{creditorId}")
    public ResponseEntity<String> deleteDebtCaseById(@Valid
                                                     @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                     @PathVariable(name = "id") int id,
                                                     @Valid
                                                     @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                     @PathVariable(name = "creditorId") int creditorId) {
        if (Boolean.TRUE.equals(debtCaseService.deleteDebtCaseById(id, creditorId))) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }
}