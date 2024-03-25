package com.dm.debtease.service;

import com.dm.debtease.TestUtils;
import com.dm.debtease.model.DebtCase;
import com.dm.debtease.service.impl.EmailServiceImpl;
import com.dm.debtease.utils.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {
    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Test
    void testSendEmailNotification() {
        int creditorId = 1;
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
        DebtCase expectedDebtCase =
                TestUtils.setupDebtCaseTestData(creditorUsername, creditorId, debtorName, debtorSurname, debtorEmail,
                        debtorPhoneNumber, typeToMatch, status, dueDate, lateInterestRate, amountOwed, debtorUsername);
        emailService.sendNotificationEmail(expectedDebtCase);
        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }
}
