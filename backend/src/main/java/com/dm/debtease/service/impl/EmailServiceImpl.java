package com.dm.debtease.service.impl;

import com.dm.debtease.model.Creditor;
import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.Debtor;
import com.dm.debtease.service.EmailService;
import com.dm.debtease.utils.Constants;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
@SuppressWarnings("unused")
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String infoEmailUsername;
    @Value("${spring.environment}")
    private String environment;

    @Override
    public void sendNotificationEmail(DebtCase debtCase) {
        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true);
            if (infoEmailUsername == null) {
                infoEmailUsername = Constants.DEBT_EASE_EMAIL;
            }
            helper.setFrom(new InternetAddress(infoEmailUsername));
            Debtor debtor = debtCase.getDebtor();
            Creditor creditor = debtCase.getCreditor();
            if (Constants.TEST_ENVIRONMENT.equals(environment)) {
                helper.setTo(infoEmailUsername);
            } else {
                helper.setTo(debtor.getEmail());
            }
            helper.setSubject(String.format("Pending debt until %s from %s",
                    debtCase.getDueDate().format(Constants.DATE_TIME_FORMATTER), creditor.getName()));
            String htmlContent = String.format(
                    """
                            <html>
                            <body>
                                <h2>Dear, %s %s!</h2>
                                <p>You have an open debt case with an amount owed of %.2f!</p>
                                <p>Issued by %s.</p>
                                <p>Please pay by %s!</p>
                                <p>Best wishes,</p>
                                <p>Debt Ease</p>
                            </body>
                            </html>
                            """,
                    debtor.getName(), debtor.getSurname(),
                    debtCase.getAmountOwed(), creditor.getName(),
                    debtCase.getDueDate().format(Constants.DATE_TIME_FORMATTER));
            helper.setText(htmlContent, true);
            javaMailSender.send(mailMessage);
        } catch (MessagingException e) {
            log.error("Error sending email {}", e.getMessage());
        }
    }
}
