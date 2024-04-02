package com.dm.debtease.service.impl;

import com.dm.debtease.model.Creditor;
import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.Debtor;
import com.dm.debtease.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String infoEmailUsername;

    @Override
    public void sendNotificationEmail(DebtCase debtCase) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(infoEmailUsername);
        Debtor debtor = debtCase.getDebtor();
        Creditor creditor = debtCase.getCreditor();
        mailMessage.setTo(debtor.getEmail());
        mailMessage.setSubject(
                String.format("Pending debt until %s from %s", debtCase.getDueDate(), creditor.getName()));
        mailMessage.setText(String.format(
                "Dear, %s %s! You have an open debt case with an amount owed: %f.2f! Issued by %s. Please pay by %s!",
                debtor.getName(), debtor.getSurname(),
                debtCase.getAmountOwed(), creditor.getName(),
                debtCase.getDueDate()));
        javaMailSender.send(mailMessage);
    }
}
