package com.dm.debtease.controller;

import com.dm.debtease.model.Creditor;
import com.dm.debtease.model.dto.CreditorDTO;
import com.dm.debtease.service.CreditorService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@Validated
@CrossOrigin
@RequestMapping(value = "/api/creditor")
@SecurityRequirement(name = "dmapi")
public class CreditorController {
    private final CreditorService creditorService;
    @Autowired
    public CreditorController(CreditorService creditorService) {
        this.creditorService = creditorService;
    }
    @GetMapping("/all")
    public ResponseEntity<List<Creditor>> getAllCreditors() {
        List<Creditor> creditors = creditorService.getAllCreditors();
        if (Objects.nonNull(creditors)) {
            return ResponseEntity.ok(creditors);
        }

        return ResponseEntity.badRequest().build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Creditor> getCreditorById(@Valid
                                                        @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                        @NotNull @PathVariable(name = "id") int id) {
        Creditor creditor = creditorService.getCreditorById(id);
        if (Objects.nonNull(creditor)) {
            return ResponseEntity.ok(creditor);
        }

        return ResponseEntity.badRequest().build();
    }
    @GetMapping("/username/{username}")
    public ResponseEntity<Creditor> getCreditorByUsername(@Valid
                                                              @NotBlank
                                                              @PathVariable(name = "username") String username) {
        Creditor creditor = creditorService.getCreditorByUsername(username);
        if (Objects.nonNull(creditor)) {
            return ResponseEntity.ok(creditor);
        }

        return ResponseEntity.badRequest().build();
    }
    @PutMapping("/edit/{id}")
    public ResponseEntity<Creditor> editCreditorById(@Valid @RequestBody CreditorDTO creditorDTO,
                                                     @Valid
                                                     @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                     @NotNull @PathVariable(name = "id") int id) {
        Creditor creditor = creditorService.editCreditorById(creditorDTO, id);

        if (Objects.nonNull(creditor)) {
            return ResponseEntity.ok(creditor);
        }

        return ResponseEntity.badRequest().build();
    }
    @PostMapping("/create")
    public ResponseEntity<Creditor> createCreditor(@Valid @RequestBody CreditorDTO creditorDTO) {
        Creditor creditor = creditorService.createCreditor(creditorDTO);

        if (Objects.nonNull(creditor)) {
            return ResponseEntity.ok(creditor);
        }

        return ResponseEntity.badRequest().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCreditorById(@Valid
                                                         @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                         @NotNull @PathVariable(name = "id") int id) {
        if (Boolean.TRUE.equals(creditorService.deleteCreditorById(id))) {
                return ResponseEntity.ok(String.format("Creditor with id %d deleted successfully", id));
        }

        return ResponseEntity.badRequest().body("Remove creditor from debtcases!");
    }
}