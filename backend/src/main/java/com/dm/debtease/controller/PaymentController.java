package com.dm.debtease.controller;

import com.dm.debtease.model.Payment;
import com.dm.debtease.model.dto.PaymentRequestDTO;
import com.dm.debtease.service.PaymentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@CrossOrigin
@RequiredArgsConstructor
@SecurityRequirement(name = "dmapi")
@RequestMapping(value = "/api/payments")
@SuppressWarnings("unused")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/{username}")
    public ResponseEntity<List<Payment>> getPaymentsByDebtorUsername(@Valid
                                                                         @NotBlank
                                                                         @PathVariable(name = "username") String username) {
        List<Payment> payments = paymentService.getAllDebtorPayments(username);
        return ResponseEntity.ok(payments);
    }

    @PostMapping(value = "/{id}/pay")
    public ResponseEntity<String> payForDebtCaseById(@Valid @RequestBody PaymentRequestDTO paymentRequestDTO, BindingResult result,
                                                     @Valid
                                                     @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                     @PathVariable(name = "id") int id) {
        if (paymentService.isPaymentMade(paymentRequestDTO, id)) {
            return ResponseEntity.status(HttpStatus.OK).body("Payment was successful");
        }
        return ResponseEntity.badRequest().build();
    }
}
