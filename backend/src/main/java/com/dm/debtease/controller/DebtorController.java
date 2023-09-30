package com.dm.debtease.controller;

import com.dm.debtease.model.Debtor;
import com.dm.debtease.model.dto.DebtorDTO;
import com.dm.debtease.service.DebtorService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@CrossOrigin
@RequestMapping(value = "/api/debtor")
@SecurityRequirement(name = "dmapi")
public class DebtorController {
    private final DebtorService debtorService;

    @Autowired
    public DebtorController(DebtorService debtorService) {
        this.debtorService = debtorService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Debtor>> getAllDebtors() {
        List<Debtor> debtors = debtorService.getAllDebtors();
        return ResponseEntity.ok(debtors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Debtor> getDebtorById(@Valid
                                                @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                @NotNull @PathVariable(name = "id") int id) {
        Debtor debtor = debtorService.getDebtorById(id);
        return ResponseEntity.ok(debtor);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Debtor> getDebtorByUsername(@Valid
                                                      @NotBlank
                                                      @PathVariable(name = "username") String username) {
        Debtor debtor = debtorService.getDebtorByUsername(username);
        return ResponseEntity.ok(debtor);
    }

    @PutMapping(value = {"/{id}", "/{id}/debtcase/{debtcaseId}/creditor/{creditorId}"})
    public ResponseEntity<Debtor> editDebtorById(@Valid @RequestBody DebtorDTO debtorDTO,
                                                 @Valid
                                                 @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                 @NotNull @PathVariable(name = "id") int id,
                                                 @Valid
                                                 @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                 @NotNull @PathVariable(name = "debtcaseId", required = false) Integer debtcaseId,
                                                 @Valid
                                                 @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                 @NotNull @PathVariable(name = "creditorId", required = false) Integer creditorId) {
        Debtor debtor;
        if (debtcaseId != null && creditorId != null) {
            debtor = debtorService.editDebtorById(debtorDTO, id, debtcaseId, creditorId);
        } else {
            debtor = debtorService.editDebtorById(debtorDTO, id);
        }
        return ResponseEntity.ok(debtor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDebtorById(@Valid
                                                   @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                   @NotNull @PathVariable(name = "id") int id) {
        if (Boolean.TRUE.equals(debtorService.deleteDebtorById(id))) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.badRequest().body("Error");
    }
}