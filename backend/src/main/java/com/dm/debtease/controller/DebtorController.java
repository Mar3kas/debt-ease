package com.dm.debtease.controller;

import com.dm.debtease.model.Debtor;
import com.dm.debtease.model.dto.DebtorDTO;
import com.dm.debtease.service.DebtorService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
@RequestMapping(value = "/api")
@SecurityRequirement(name = "dmapi")
public class DebtorController {
    private final DebtorService debtorService;

    @Autowired
    public DebtorController(DebtorService debtorService) {
        this.debtorService = debtorService;
    }

    @GetMapping("/debtors")
    public ResponseEntity<List<Debtor>> getAllDebtors() {
        List<Debtor> debtors = debtorService.getAllDebtors();
        return ResponseEntity.ok(debtors);
    }

    @GetMapping(value = {"/debtors/{id}", "/debtors/{username}"})
    public ResponseEntity<Debtor> getDebtorById(@Valid
                                                @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                @PathVariable(name = "id", required = false) Integer id,
                                                @Valid
                                                @NotBlank
                                                @PathVariable(name = "username", required = false) String username) {
        Debtor debtor = null;

        if (id != null) {
            debtor = debtorService.getDebtorById(id);
        } else if (username != null) {
            debtor = debtorService.getDebtorByUsername(username);
        }

        return ResponseEntity.ok(debtor);
    }

    @GetMapping("/debtors/profile/{username}")
    public ResponseEntity<Debtor> getDebtorByUsername(@Valid
                                                      @NotBlank
                                                      @PathVariable(name = "username") String username) {
        Debtor debtor = debtorService.getDebtorByUsername(username);
        return ResponseEntity.ok(debtor);
    }

    @PutMapping(value = {"/debtors/{id}", "/creditor/{creditorId}/debtcase/{debtcaseId}/debtors/{id}"})
    public ResponseEntity<Debtor> editDebtorById(@Valid @RequestBody DebtorDTO debtorDTO, BindingResult result,
                                                 @Valid
                                                 @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                 @PathVariable(name = "id") int id,
                                                 @Valid
                                                 @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                 @PathVariable(name = "debtcaseId", required = false) Integer debtcaseId,
                                                 @Valid
                                                 @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                 @PathVariable(name = "creditorId", required = false) Integer creditorId) {
        Debtor debtor;
        if (debtcaseId != null && creditorId != null) {
            debtor = debtorService.editDebtorById(debtorDTO, id, debtcaseId, creditorId);
        } else {
            debtor = debtorService.editDebtorById(debtorDTO, id);
        }
        return ResponseEntity.ok(debtor);
    }

    @DeleteMapping(value = {"/{id}", "/creditor/{creditorId}/debtcase/{debtcaseId}/debtors/{id}"})
    public ResponseEntity<String> deleteDebtorById(@Valid
                                                   @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                   @PathVariable(name = "id") int id,
                                                   @Valid
                                                   @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                   @PathVariable(name = "debtcaseId", required = false) Integer debtcaseId,
                                                   @Valid
                                                   @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                   @PathVariable(name = "creditorId", required = false) Integer creditorId) {
        if (debtcaseId != null && creditorId != null) {
            if (Boolean.TRUE.equals(debtorService.deleteDebtorById(id, debtcaseId, creditorId))) {
                return ResponseEntity.noContent().build();
            }
        } else if (Boolean.TRUE.equals(debtorService.deleteDebtorById(id))) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.badRequest().body("Error");
    }
}