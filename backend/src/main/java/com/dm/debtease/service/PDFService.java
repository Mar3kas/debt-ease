package com.dm.debtease.service;

import com.dm.debtease.model.DebtCase;
import com.itextpdf.text.DocumentException;
import org.knowm.xchart.PieChart;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface PDFService {
    ByteArrayInputStream generatePdf(String username) throws IOException, DocumentException;
    PieChart generatePieDiagram(List<DebtCase> debtCases);
}
