package com.dm.debtease.controller;

import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.dto.DebtCaseDTO;
import com.dm.debtease.service.DebtCaseService;
import com.opencsv.exceptions.CsvValidationException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import java.util.Objects;

@RestController
@Validated
@CrossOrigin
@RequestMapping(value = "/api/debtcase")
@SecurityRequirement(name = "dmapi")
public class DebtCaseController {
    private final DebtCaseService debtCaseService;
    @Autowired
    public DebtCaseController(DebtCaseService debtCaseService) {
        this.debtCaseService = debtCaseService;
    }
    @GetMapping("/all")
    public ResponseEntity<List<DebtCase>> getAllDebtCases() {
        List<DebtCase> debtCases = debtCaseService.getAllDebtCases();
        if (Objects.nonNull(debtCases)) {
            return ResponseEntity.ok(debtCases);
        }

        return ResponseEntity.badRequest().build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<DebtCase> getDebtCaseById(@Valid
                                                        @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                        @NotNull @PathVariable(name = "id") int id) {
        DebtCase debtCase = debtCaseService.getDebtCaseById(id);
        if (Objects.nonNull(debtCase)) {
            return ResponseEntity.ok(debtCase);
        }

        return ResponseEntity.badRequest().build();
    }
    @GetMapping("/debtor/{id}")
    public ResponseEntity<List<DebtCase>> getDebtCasesByDebtorId(@Valid
                                                                    @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                                    @NotNull @PathVariable(name = "id") int id) {
        List<DebtCase> debtCases = debtCaseService.getDebtCasesByDebtorId(id);
        if (Objects.nonNull(debtCases)) {
            return ResponseEntity.ok(debtCases);
        }

        return ResponseEntity.badRequest().build();
    }
    @GetMapping("/creditor/{id}")
    public ResponseEntity<List<DebtCase>> getDebtCasesByCreditorId(@Valid
                                                                      @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                                      @NotNull @PathVariable(name = "id") int id) {
        List<DebtCase> debtCases = debtCaseService.getDebtCasesByCreditorId(id);
        if (Objects.nonNull(debtCases)) {
            return ResponseEntity.ok(debtCases);
        }

        return ResponseEntity.badRequest().build();
    }
    @PutMapping("/edit/{id}/debtor/{debtorId}/type/{typeId}")
    public ResponseEntity<DebtCase> editDebtCaseById(@Valid @RequestBody DebtCaseDTO debtCaseDTO,
                                                     @Valid
                                                        @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                        @NotNull @PathVariable(name = "id") int id,
                                                     @Valid
                                                         @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                         @NotNull @PathVariable(name = "debtorId", required = false) int debtorId,
                                                     @Valid
                                                         @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                         @NotNull @PathVariable(name = "typeId", required = false) int typeId) {
        DebtCase debtCase = debtCaseService.editDebtCaseById(debtCaseDTO, id, debtorId, typeId);

        if (Objects.nonNull(debtCase)) {
            return ResponseEntity.ok(debtCase);
        }

        return ResponseEntity.badRequest().build();
    }
    @PostMapping(value = "/creditor/{id}/upload/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createDebtCase(@Valid
                                                     @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                     @NotNull @PathVariable(name = "id") int id, @RequestParam(name = "file") MultipartFile file) throws CsvValidationException, IOException {
        List<DebtCase> debtCases = debtCaseService.createDebtCase(file, id);
        if (Objects.nonNull(debtCases)) {
            return ResponseEntity.ok("Debtcases were successfully uploaded");
        }

        return ResponseEntity.badRequest().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDebtCaseById(@Valid
                                                     @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                     @NotNull @PathVariable(name = "id") int id) {
        if (Boolean.TRUE.equals(debtCaseService.deleteDebtCaseById(id))) {
            return ResponseEntity.ok(String.format("Debtcase with id %d deleted successfully", id));
        }

        return ResponseEntity.badRequest().build();
    }
}