package com.dm.debtease.controller;

import com.dm.debtease.service.FileReaderService;
import com.dm.debtease.utils.Constants;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@Validated
@CrossOrigin
@RequiredArgsConstructor
@SecurityRequirement(name = "dmapi")
@RequestMapping(value = "/api/files")
@SuppressWarnings("unused")
public class FileController {
    private final FileReaderService fileReaderService;

    @GetMapping("/debt/case/example")
    public ResponseEntity<InputStreamResource> getDebtCaseCSVExample() throws IOException {
        ByteArrayInputStream document = fileReaderService.readFileData(Constants.CSV_EXAMPLE_PATH);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Disposition", "attachment; filename=debt_case_upload_example.csv");
        httpHeaders.setContentType(MediaType.valueOf("text/csv"));
        return ResponseEntity.ok().headers(httpHeaders).body(new InputStreamResource(document));
    }

    @GetMapping("/agreement/form")
    public ResponseEntity<InputStreamResource> getAgreementFormExample() throws IOException {
        ByteArrayInputStream document = fileReaderService.readFileData(Constants.REQUEST_FORM_EXAMPLE_PATH);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Disposition", "attachment; filename=agreement_form.docx");
        httpHeaders.setContentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        return ResponseEntity.ok().headers(httpHeaders).body(new InputStreamResource(document));
    }
}
