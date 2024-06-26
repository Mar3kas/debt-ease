package com.dm.debtease.controller;

import com.dm.debtease.exception.InvalidFileFormatException;
import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.DebtPaymentStrategy;
import com.dm.debtease.model.dto.DebtCaseDTO;
import com.dm.debtease.model.dto.DebtPaymentStrategyDTO;
import com.dm.debtease.service.CSVService;
import com.dm.debtease.service.DebtCaseService;
import com.dm.debtease.service.PDFService;
import com.dm.debtease.utils.Constants;
import com.itextpdf.text.DocumentException;
import com.opencsv.exceptions.CsvValidationException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@CrossOrigin
@RequiredArgsConstructor
@SecurityRequirement(name = "dmapi")
@RequestMapping(value = "/api/debt/cases")
@SuppressWarnings("unused")
public class DebtCaseController {
    private final DebtCaseService debtCaseService;
    private final CSVService csvService;
    private final PDFService pdfService;

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

    @PostMapping("/debtor/{username}/payment/strategy")
    public ResponseEntity<DebtPaymentStrategy> calculateDebtCasesPaymentStrategiesUntilPayedOffByDebtorUsername(@Valid @RequestBody DebtPaymentStrategyDTO debtPaymentStrategyDTO, BindingResult result,
                                                                                                          @Valid @PathVariable(name = "username") String username) {
        DebtPaymentStrategy debtPaymentStrategy = debtCaseService.calculateDebtPaymentStrategies(debtPaymentStrategyDTO, username);
        return ResponseEntity.ok(debtPaymentStrategy);
    }

    @GetMapping("/generate/report/debtor/{username}")
    public ResponseEntity<InputStreamResource> getDebtCasesReportByDebtorUsername(@Valid
                                                                           @PathVariable(name = "username") String username) throws IOException, DocumentException {
        ByteArrayInputStream document = pdfService.generatePdf(username);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Disposition", "attachment; filename=" + "debt_cases_report_" + LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER_FOR_FILE) + ".pdf");
        httpHeaders.setContentType(MediaType.APPLICATION_PDF);
        return ResponseEntity.ok().headers(httpHeaders).body(new InputStreamResource(document));
    }

    @PutMapping("/{id}/creditors/{creditorId}")
    public ResponseEntity<DebtCase> editDebtCaseById(@Valid @RequestBody DebtCaseDTO debtCaseDTO, BindingResult result,
                                                     @Valid
                                                     @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                     @PathVariable(name = "id") int id,
                                                     @Valid
                                                     @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                     @PathVariable(name = "creditorId") int creditorId) {
        DebtCase debtCase = debtCaseService.editDebtCaseByIdAndCreditorId(debtCaseDTO, id, creditorId);
        return ResponseEntity.ok(debtCase);
    }

    @PostMapping(value = "/creditors/{username}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createDebtCaseByCreditorUsername(@Valid
                                                 @NotBlank
                                                 @PathVariable(name = "username") String username,
                                                 @RequestParam(name = "file") MultipartFile file) throws CsvValidationException, IOException, InvalidFileFormatException {
        csvService.readCsvDataAndSendToKafka(file, username);
        return ResponseEntity.status(HttpStatus.CREATED).body("CSV uploaded successfully and debt cases are being enriched");
    }

    @DeleteMapping("/{id}/creditors/{creditorId}")
    public ResponseEntity<String> deleteDebtCaseById(@Valid
                                                     @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                     @PathVariable(name = "id") int id,
                                                     @Valid
                                                     @Min(value = 1, message = "ID must be a non-negative integer and greater than 0")
                                                     @PathVariable(name = "creditorId") int creditorId) {
        if (Boolean.TRUE.equals(debtCaseService.deleteDebtCaseByIdAndCreditorId(id, creditorId))) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }
}