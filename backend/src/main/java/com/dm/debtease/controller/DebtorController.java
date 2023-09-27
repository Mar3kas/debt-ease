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
import java.util.Objects;

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
        if (Objects.nonNull(debtors)) {
            return ResponseEntity.ok(debtors);
        }

        return ResponseEntity.badRequest().build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Debtor> getDebtorById(@Valid
                                                    @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                    @NotNull @PathVariable(name = "id") int id) {
        Debtor debtor = debtorService.getDebtorById(id);
        if (Objects.nonNull(debtor)) {
            return ResponseEntity.ok(debtor);
        }

        return ResponseEntity.badRequest().build();
    }
    @GetMapping("/username/{username}")
    public ResponseEntity<Debtor> getDebtorByUsername(@Valid
                                                          @NotBlank
                                                          @PathVariable(name = "username") String username) {
        Debtor debtor = debtorService.getDebtorByUsername(username);
        if (Objects.nonNull(debtor)) {
            return ResponseEntity.ok(debtor);
        }

        return ResponseEntity.badRequest().build();
    }
    @PutMapping("/edit/{id}")
    public ResponseEntity<Debtor> editDebtorById(@Valid @RequestBody DebtorDTO debtorDTO,
                                                     @Valid
                                                     @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                     @NotNull @PathVariable(name = "id") int id) {
        Debtor debtor = debtorService.editDebtorById(debtorDTO, id);

        if (Objects.nonNull(debtor)) {
            return ResponseEntity.ok(debtor);
        }

        return ResponseEntity.badRequest().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCreditorById(@Valid
                                                     @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                     @NotNull @PathVariable(name = "id") int id) {
        if (Boolean.TRUE.equals(debtorService.deleteDebtorById(id))) {
            return ResponseEntity.ok(String.format("Debtor with id %d deleted successfully", id));
        }

        return ResponseEntity.badRequest().body("Remove debtor from debtcases!");
    }
}