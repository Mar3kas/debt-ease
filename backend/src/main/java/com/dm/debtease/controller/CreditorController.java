package com.dm.debtease.controller;

import com.dm.debtease.model.Creditor;
import com.dm.debtease.model.dto.CreditorDTO;
import com.dm.debtease.service.CreditorService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@CrossOrigin
@RequiredArgsConstructor
@SecurityRequirement(name = "dmapi")
@RequestMapping(value = "/api/creditors")
public class CreditorController {
    private final CreditorService creditorService;

    @GetMapping()
    public ResponseEntity<List<Creditor>> getAllCreditors() {
        List<Creditor> creditors = creditorService.getAllCreditors();
        return ResponseEntity.ok(creditors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Creditor> getCreditorById(@Valid
                                                    @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                    @PathVariable(name = "id") int id) {
        Creditor creditor = creditorService.getCreditorById(id);
        return ResponseEntity.ok(creditor);
    }

    @GetMapping("/{username}")
    public ResponseEntity<Creditor> getCreditorByUsername(@Valid
                                                          @NotBlank
                                                          @PathVariable(name = "username") String username) {
        Creditor creditor = creditorService.getCreditorByUsername(username);
        return ResponseEntity.ok(creditor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Creditor> editCreditorById(@Valid @RequestBody CreditorDTO creditorDTO, BindingResult result,
                                                     @Valid
                                                     @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                     @PathVariable(name = "id") int id) {

        Creditor creditor = creditorService.editCreditorById(creditorDTO, id);
        return ResponseEntity.ok(creditor);
    }

    @PostMapping()
    public ResponseEntity<Creditor> createCreditor(@Valid @RequestBody CreditorDTO creditorDTO, BindingResult result) {
        Creditor creditor = creditorService.createCreditor(creditorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(creditor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCreditorById(@Valid
                                                     @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                     @PathVariable(name = "id") int id) {
        if (Boolean.TRUE.equals(creditorService.deleteCreditorById(id))) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.badRequest().body("Remove creditor from debtcases!");
    }
}