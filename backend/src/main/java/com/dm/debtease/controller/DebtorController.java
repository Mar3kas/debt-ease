package com.dm.debtease.controller;

import com.dm.debtease.model.Debtor;
import com.dm.debtease.model.dto.DebtorDTO;
import com.dm.debtease.service.DebtorService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@CrossOrigin
@RequiredArgsConstructor
@SecurityRequirement(name = "dmapi")
@RequestMapping(value = "/api/debtors")
public class DebtorController {
    private final DebtorService debtorService;

    @GetMapping()
    public ResponseEntity<List<Debtor>> getAllDebtors() {
        List<Debtor> debtors = debtorService.getAllDebtors();
        return ResponseEntity.ok(debtors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Debtor> getDebtorById(@Valid
                                                @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                @PathVariable(name = "id") int id) {
        Debtor debtor = debtorService.getDebtorById(id);
        return ResponseEntity.ok(debtor);
    }

    @GetMapping("/{username}")
    public ResponseEntity<Object> getDebtorByUsername(@Valid
                                                      @NotBlank
                                                      @PathVariable(name = "username") String username) {
        Debtor debtor = debtorService.getDebtorByUsername(username);
        return ResponseEntity.ok(debtor);
    }

    @PutMapping("/debtors/{id}")
    public ResponseEntity<Debtor> editDebtorById(@Valid @RequestBody DebtorDTO debtorDTO, BindingResult result,
                                                 @Valid
                                                 @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                 @PathVariable(name = "id") int id) {
        Debtor debtor = debtorService.editDebtorById(debtorDTO, id);
        return ResponseEntity.ok(debtor);
    }

    @DeleteMapping("/debtors/{id}")
    public ResponseEntity<Map<String, String>> deleteDebtorById(@Valid
                                                                @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                                @PathVariable(name = "id") int id) {
        if (Boolean.TRUE.equals(debtorService.deleteDebtorById(id))) {
            return ResponseEntity.noContent().build();
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Error deleting debtor!");

        return ResponseEntity.badRequest().body(response);
    }
}