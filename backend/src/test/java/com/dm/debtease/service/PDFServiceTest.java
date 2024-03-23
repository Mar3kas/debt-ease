package com.dm.debtease.service;

import com.dm.debtease.TestUtils;
import com.dm.debtease.model.DebtCase;
import com.dm.debtease.service.impl.PDFServiceImpl;
import com.dm.debtease.utils.Constants;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.knowm.xchart.PieChart;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PDFServiceTest {
    @Mock
    private DebtCaseService debtCaseService;
    @InjectMocks
    private PDFServiceImpl pdfService;

    @Test
    void generatePDFNoDebtCases() {
        String username = "userWithoutDebts";
        when(debtCaseService.getDebtCasesByDebtorUsername(username)).thenReturn(Collections.emptyList());
        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> pdfService.generatePdf(username),
                "Expected generatePdf to throw, but it didn't"
        );
        Assertions.assertTrue(thrown.getMessage().contains(String.format(Constants.DEBT_CASES_EMPTY, username)));
    }

    @Test
    void generatePPDFWithDebtCases() throws Exception {
        String debtorUsername = "userWithDebts";
        String debtorName = "name";
        String debtorSurname = "surname";
        String debtorEmail = "email@gmail.com";
        String debtorPhoneNumber = "+37067144213";
        String creditorUsername = "creditor123";
        String status = "NEW";
        String typeToMatch = "DEFAULT_DEBT";
        LocalDateTime dueDate = LocalDateTime.parse(LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER),
                Constants.DATE_TIME_FORMATTER);
        double lateInterestRate = 10.0;
        BigDecimal amountOwed = BigDecimal.valueOf(35.53);
        int id = 1;
        List<DebtCase> expectedDebtCases =
                List.of(TestUtils.setupDebtCaseTestData(creditorUsername, id, debtorName, debtorSurname, debtorEmail,
                        debtorPhoneNumber, typeToMatch, status, dueDate, lateInterestRate, amountOwed));
        when(debtCaseService.getDebtCasesByDebtorUsername(debtorUsername)).thenReturn(expectedDebtCases);
        ByteArrayInputStream pdfStream = pdfService.generatePdf(debtorUsername);
        PdfReader pdfReader = new PdfReader(pdfStream);
        StringBuilder pdfText = new StringBuilder();
        for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
            pdfText.append(PdfTextExtractor.getTextFromPage(pdfReader, i).trim());
        }
        Assertions.assertTrue(pdfText.toString().contains(Constants.GENERATED_PDF_INTRO_MESSAGE));
        Assertions.assertTrue(pdfText.toString().contains(Constants.GENERATED_PDF_TITLE));
        Assertions.assertTrue(pdfText.toString().contains(Constants.GENERATED_PDF_DISCLAIMER));
        Assertions.assertTrue(pdfText.toString()
                .contains(String.format(Constants.GENERATED_PDF_GREETING_MESSAGE, debtorName, debtorSurname)));
        Assertions.assertTrue(pdfText.toString().contains(expectedDebtCases.get(0).getDebtor().getName()));
        Assertions.assertTrue(pdfText.toString().contains(expectedDebtCases.get(0).getDebtor().getSurname()));
        Assertions.assertTrue(pdfText.toString().contains(expectedDebtCases.get(0).getAmountOwed().toString()));
        Assertions.assertTrue(pdfText.toString().contains(expectedDebtCases.get(0).getCreditor().getName()));
        pdfReader.close();
    }

    @Test
    void testGeneratePieChart()
    {
        String debtorName = "name";
        String debtorSurname = "surname";
        String debtorEmail = "email@gmail.com";
        String debtorPhoneNumber = "+37067144213";
        String creditorUsername = "creditor123";
        String status = "NEW";
        String typeToMatch = "DEFAULT_DEBT";
        LocalDateTime dueDate = LocalDateTime.parse(LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER),
                Constants.DATE_TIME_FORMATTER);
        double lateInterestRate = 10.0;
        BigDecimal amountOwed = BigDecimal.valueOf(35.53);
        int id = 1;
        List<DebtCase> expectedDebtCases =
                List.of(TestUtils.setupDebtCaseTestData(creditorUsername, id, debtorName, debtorSurname, debtorEmail,
                        debtorPhoneNumber, typeToMatch, status, dueDate, lateInterestRate, amountOwed));
        PieChart pieChart = pdfService.generatePieDiagram(expectedDebtCases);
        Assertions.assertNotNull(pieChart);
        Assertions.assertEquals(1, pieChart.getSeriesMap().size(), "There should be 1 type of debt cases.");
        Assertions.assertTrue(pieChart.getSeriesMap().containsKey("Default Debt"), "Default debt series is missing.");
        Assertions.assertEquals(1, pieChart.getSeriesMap().get("Default Debt").getValue().intValue());
    }
}
