package com.dm.debtease.scheduler;

import com.dm.debtease.TestUtils;
import com.dm.debtease.model.DebtCase;
import com.dm.debtease.repository.DebtCaseRepository;
import com.dm.debtease.service.DebtCaseService;
import com.dm.debtease.utils.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unused")
public class SchedulerTest {
    @Mock
    private DebtCaseService debtCaseService;
    @Mock
    private DebtCaseRepository debtCaseRepository;
    @Mock
    private JavaMailSender javaMailSender;
    @InjectMocks
    private Scheduler scheduler;
    @Captor
    private ArgumentCaptor<DebtCase> debtCaseCaptor;

    @Test
    public void testEmailNotificationForUpcomingDueDatePaymentScheduler() {
        String debtorUsername = "debtor";
        String debtorName = "name";
        String debtorSurname = "surname";
        String debtorEmail = "email@gmail.com";
        String debtorPhoneNumber = "+37067144213";
        String creditorUsername = "creditor123";
        String status = "NEW";
        String typeToMatch = "DEFAULT_DEBT";
        LocalDateTime dueDate = LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(Constants.DATE_TIME_FORMATTER),
                Constants.DATE_TIME_FORMATTER);
        double lateInterestRate = 10.0;
        BigDecimal amountOwed = BigDecimal.valueOf(35.53);
        int id = 1;
        List<DebtCase> expectedDebtCases =
                List.of(TestUtils.setupDebtCaseTestData(creditorUsername, id, debtorName, debtorSurname, debtorEmail,
                        debtorPhoneNumber, typeToMatch, status, dueDate, lateInterestRate, amountOwed, debtorUsername));
        when(debtCaseService.getAllDebtCases()).thenReturn(expectedDebtCases);
        scheduler.emailNotificationForUpcomingDueDatePaymentScheduler();
        for (DebtCase debtCase : expectedDebtCases) {
            verify(javaMailSender).send(any(SimpleMailMessage.class));
        }
    }

    @Test
    public void testEmailNotificationEachMonth20DayScheduler() {
        String debtorUsername = "debtor";
        String debtorName = "name";
        String debtorSurname = "surname";
        String debtorEmail = "email@gmail.com";
        String debtorPhoneNumber = "+37067144213";
        String creditorUsername = "creditor123";
        String status = "NEW";
        String typeToMatch = "DEFAULT_DEBT";
        LocalDateTime dueDate = LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(Constants.DATE_TIME_FORMATTER),
                Constants.DATE_TIME_FORMATTER);
        double lateInterestRate = 10.0;
        BigDecimal amountOwed = BigDecimal.valueOf(35.53);
        int id = 1;
        List<DebtCase> expectedDebtCases =
                List.of(TestUtils.setupDebtCaseTestData(creditorUsername, id, debtorName, debtorSurname, debtorEmail,
                        debtorPhoneNumber, typeToMatch, status, dueDate, lateInterestRate, amountOwed, debtorUsername));
        when(debtCaseService.getAllDebtCases()).thenReturn(expectedDebtCases);
        scheduler.emailNotificationEachMonth20DayScheduler();
        for (DebtCase debtCase : expectedDebtCases) {
            verify(javaMailSender).send(any(SimpleMailMessage.class));
        }
    }

    @Test
    public void testCalculateOutstandingBalanceScheduler() {
        String debtorUsername = "debtor";
        String debtorName = "name";
        String debtorSurname = "surname";
        String debtorEmail = "email@gmail.com";
        String debtorPhoneNumber = "+37067144213";
        String creditorUsername = "creditor123";
        String status = "NEW";
        String typeToMatch = "DEFAULT_DEBT";
        LocalDateTime currentDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        LocalDateTime dueDate = LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(Constants.DATE_TIME_FORMATTER),
                Constants.DATE_TIME_FORMATTER);
        double lateInterestRate = 10.0;
        BigDecimal amountOwed = BigDecimal.valueOf(35.53);
        int id = 1;
        List<DebtCase> expectedDebtCases =
                List.of(TestUtils.setupDebtCaseTestData(creditorUsername, id, debtorName, debtorSurname, debtorEmail,
                        debtorPhoneNumber, typeToMatch, status, dueDate, lateInterestRate, amountOwed, debtorUsername));
        when(debtCaseRepository.findByDueDateLessThanEqual(currentDate)).thenReturn(Optional.of(expectedDebtCases));
        scheduler.calculateOutstandingBalanceScheduler();
        verify(debtCaseRepository, times(expectedDebtCases.size())).save(debtCaseCaptor.capture());
        List<DebtCase> capturedDebtCases = debtCaseCaptor.getAllValues();
        Assertions.assertEquals(amountOwed.multiply(BigDecimal.valueOf(lateInterestRate/ 100.0)).add(BigDecimal.TEN), capturedDebtCases.get(0).getOutstandingBalance());
    }
}
