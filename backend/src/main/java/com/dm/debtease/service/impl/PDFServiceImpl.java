package com.dm.debtease.service.impl;

import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.DebtCaseStatus;
import com.dm.debtease.service.DebtCaseService;
import com.dm.debtease.service.DebtCaseTypeService;
import com.dm.debtease.service.PDFService;
import com.dm.debtease.utils.Constants;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.style.PieStyler;
import org.knowm.xchart.style.Styler;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PDFServiceImpl implements PDFService {
    private final DebtCaseService debtCaseService;
    private final DebtCaseTypeService debtCaseTypeService;

    @Override
    public ByteArrayInputStream generatePdf(String username) throws IOException, DocumentException {
        List<DebtCase> debtCases = debtCaseService.getDebtCasesByDebtorUsername(username)
                .stream()
                .filter(debtCase -> !DebtCaseStatus.CLOSED.equals(debtCase.getDebtCaseStatus()))
                .collect(Collectors.toList());
        if (debtCases.isEmpty()) {
            throw new EntityNotFoundException(String.format(Constants.DEBT_CASES_EMPTY, username));
        }
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);
        document.open();
        Image img = Image.getInstance("src/main/resources/images/debtease.png");
        img.scaleToFit(100, 100);
        img.setAbsolutePosition(36, 770);
        document.add(img);
        document.add(new Paragraph("\n"));
        addTitle(document);
        addIntro(document, debtCases.get(0).getDebtor().getName(), debtCases.get(0).getDebtor().getSurname());
        addDebtCasesInfo(document, debtCases);
        addTotalDebt(document, calculateTotalDebt(debtCases), calculateOutstanding(debtCases));
        addDisclaimer(document);
        addPieChart(document, generatePieDiagram(debtCases));
        document.close();
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    @Override
    public PieChart generatePieDiagram(List<DebtCase> debtCases) {
        PieChart chart = new PieChartBuilder()
                .width(450)
                .height(450)
                .title("Debt Cases Distribution")
                .build();
        chart.getStyler().setChartBackgroundColor(Color.WHITE);
        chart.getStyler().setLegendVisible(true);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideS);
        chart.getStyler().setLabelType(PieStyler.LabelType.NameAndValue);
        Map<String, Long> typeCountMap = debtCases.stream()
                .collect(Collectors.groupingBy(debtCase -> debtCaseTypeService.formatDebtCaseType(debtCase.getDebtCaseType().getType()),
                        Collectors.counting()));
        typeCountMap.forEach((debtTypeName, count) -> chart.addSeries(debtTypeName, count.intValue()));
        return chart;
    }

    private void addTitle(Document document) throws DocumentException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
        Paragraph title = new Paragraph(Constants.GENERATED_PDF_TITLE, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
    }

    private void addIntro(Document document, String name, String surname) throws DocumentException {
        Font introFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
        Paragraph intro = new Paragraph();
        intro.add(
                new Phrase(String.format(Constants.GENERATED_PDF_GREETING_MESSAGE, name, surname) + "\n\n", introFont));
        intro.add(new Phrase(Constants.GENERATED_PDF_INTRO_MESSAGE + "\n\n", introFont));
        document.add(intro);
    }

    private void addDebtCasesInfo(Document document, List<DebtCase> debtCases) throws DocumentException {
        Font debtCaseFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
        for (DebtCase debtCase : debtCases) {
            Paragraph debtCaseInfo = new Paragraph();
            debtCaseInfo.add(new Chunk("Creditor: " + debtCase.getCreditor().getName(), debtCaseFont));
            debtCaseInfo.add(Chunk.NEWLINE);
            debtCaseInfo.add(
                    new Chunk("Type: " + debtCaseTypeService.formatDebtCaseType(debtCase.getDebtCaseType().getType()), debtCaseFont));
            debtCaseInfo.add(Chunk.NEWLINE);
            debtCaseInfo.add(new Chunk(
                    "Due Date: " + debtCase.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    debtCaseFont));
            debtCaseInfo.add(Chunk.NEWLINE);
            debtCaseInfo.add(new Chunk("Late Interest Rate: " + debtCase.getLateInterestRate() + "%", debtCaseFont));
            debtCaseInfo.add(Chunk.NEWLINE);
            if (debtCase.getOutstandingBalance().compareTo(BigDecimal.ZERO) > 0) {
                debtCaseInfo.add(new Chunk("Initial Amount Owed: " + debtCase.getAmountOwed() + "€", debtCaseFont));
                debtCaseInfo.add(Chunk.NEWLINE);
                Font outstandingAmountOwedFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.RED);
                debtCaseInfo.add(new Chunk("Outstanding Amount Owed: " + debtCase.getOutstandingBalance() + "€",
                        outstandingAmountOwedFont));
                debtCaseInfo.add(Chunk.NEWLINE);
            } else {
                debtCaseInfo.add(new Chunk("Amount Owed: " + debtCase.getAmountOwed() + "€", debtCaseFont));
                debtCaseInfo.add(Chunk.NEWLINE);
            }
            document.add(debtCaseInfo);
            document.add(Chunk.NEWLINE);
        }
    }

    private void addTotalDebt(Document document, BigDecimal totalDebtAmount, BigDecimal totalOutstandingAmount)
            throws DocumentException {
        Font totalDebtFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.RED);
        Paragraph totalDebt =
                new Paragraph("Total Amount of Owned Debt Money: " + totalDebtAmount + "€", totalDebtFont);
        totalDebt.setAlignment(Element.ALIGN_CENTER);
        document.add(totalDebt);
        if (totalOutstandingAmount.compareTo(BigDecimal.ZERO) > 0) {
            Paragraph totalOutstanding =
                    new Paragraph("Total Amount of Outstanding Debt Money: " + totalOutstandingAmount + "€",
                            totalDebtFont);
            totalOutstanding.setAlignment(Element.ALIGN_CENTER);
            document.add(totalOutstanding);
        }
    }

    private void addDisclaimer(Document document) throws DocumentException {
        Font disclaimerFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
        Paragraph disclaimer = new Paragraph(Constants.GENERATED_PDF_DISCLAIMER, disclaimerFont);
        disclaimer.setAlignment(Element.ALIGN_CENTER);
        document.add(disclaimer);
    }

    private void addPieChart(Document document, PieChart pieChart) throws IOException, DocumentException {
        byte[] chartImageBytes = BitmapEncoder.getBitmapBytes(pieChart, BitmapEncoder.BitmapFormat.PNG);
        Image pieChartImage = Image.getInstance(chartImageBytes);
        pieChartImage.setAlignment(Element.ALIGN_CENTER);
        document.add(pieChartImage);
    }

    private BigDecimal calculateTotalDebt(List<DebtCase> debtCases) {
        return debtCases.stream()
                .map(debtCase -> debtCase.getOutstandingBalance().compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.ZERO :
                        debtCase.getAmountOwed())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateOutstanding(List<DebtCase> debtCases) {
        return debtCases.stream()
                .map(DebtCase::getOutstandingBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}