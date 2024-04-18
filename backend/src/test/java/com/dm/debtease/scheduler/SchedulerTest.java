package com.dm.debtease.scheduler;

import com.dm.debtease.TestUtils;
import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.DebtCaseStatus;
import com.dm.debtease.repository.DebtCaseRepository;
import com.dm.debtease.service.DebtCaseService;
import com.dm.debtease.service.EmailService;
import com.dm.debtease.utils.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private EmailService emailService;
    @InjectMocks
    private Scheduler scheduler;
    @Captor
    private ArgumentCaptor<DebtCase> debtCaseCaptor;

    @Test
    void emailNotificationForUpcomingDueDatePaymentScheduler_WhenDebtCasesPending_ShouldSendNotificationEmailForEachCase() {
        String debtorUsername = "debtor";
        String debtorName = "name";
        String debtorSurname = "surname";
        String debtorEmail = "email@gmail.com";
        String debtorPhoneNumber = "+37067144213";
        String creditorUsername = "creditor123";
        String typeToMatch = "DEFAULT_DEBT";
        LocalDateTime dueDate =
                LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(Constants.DATE_TIME_FORMATTER),
                        Constants.DATE_TIME_FORMATTER);
        double lateInterestRate = 10.0;
        double debtInterestRate = 10.0;
        BigDecimal amountOwed = BigDecimal.valueOf(35.53);
        int id = 1;
        List<DebtCase> expectedDebtCases =
                List.of(TestUtils.setupDebtCaseTestData(creditorUsername, id, debtorName, debtorSurname, debtorEmail,
                        debtorPhoneNumber, typeToMatch, DebtCaseStatus.NEW, dueDate, lateInterestRate, debtInterestRate,
                        amountOwed, debtorUsername));
        when(debtCaseService.getAllDebtCases()).thenReturn(expectedDebtCases);
        when(debtCaseService.isDebtCasePending(any(DebtCase.class), any(LocalDateTime.class),
                any(LocalDateTime.class))).thenReturn(true);

        scheduler.emailNotificationForUpcomingDueDatePaymentScheduler();

        verify(emailService, times(expectedDebtCases.size())).sendNotificationEmail(any(DebtCase.class));
    }

    @Test
    void emailNotificationEachMonth20DayScheduler_WhenCalled_ShouldSendNotificationEmailForEachCase() {
        String debtorUsername = "debtor";
        String debtorName = "name";
        String debtorSurname = "surname";
        String debtorEmail = "email@gmail.com";
        String debtorPhoneNumber = "+37067144213";
        String creditorUsername = "creditor123";
        String typeToMatch = "DEFAULT_DEBT";
        LocalDateTime dueDate =
                LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(Constants.DATE_TIME_FORMATTER),
                        Constants.DATE_TIME_FORMATTER);
        double lateInterestRate = 10.0;
        double debtInterestRate = 10.0;
        BigDecimal amountOwed = BigDecimal.valueOf(35.53);
        int id = 1;
        List<DebtCase> expectedDebtCases =
                List.of(TestUtils.setupDebtCaseTestData(creditorUsername, id, debtorName, debtorSurname, debtorEmail,
                        debtorPhoneNumber, typeToMatch, DebtCaseStatus.NEW, dueDate, lateInterestRate, debtInterestRate,
                        amountOwed, debtorUsername));
        when(debtCaseService.getAllDebtCases()).thenReturn(expectedDebtCases);

        scheduler.emailNotificationEachMonth20DayScheduler();

        verify(emailService, times(expectedDebtCases.size())).sendNotificationEmail(any(DebtCase.class));
    }

    @Test
    void calculateOutstandingBalanceScheduler_WhenDueDatePassed_ShouldCalculateOutstandingBalanceForEachCase() {
        String debtorUsername = "debtor";
        String debtorName = "name";
        String debtorSurname = "surname";
        String debtorEmail = "email@gmail.com";
        String debtorPhoneNumber = "+37067144213";
        String creditorUsername = "creditor123";
        String typeToMatch = "DEFAULT_DEBT";
        LocalDateTime currentDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        LocalDateTime dueDate =
                LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(Constants.DATE_TIME_FORMATTER),
                        Constants.DATE_TIME_FORMATTER);
        double lateInterestRate = 10.0;
        double debtInterestRate = 10.0;
        BigDecimal amountOwed = BigDecimal.valueOf(35.53);
        int id = 1;
        List<DebtCase> expectedDebtCases =
                List.of(TestUtils.setupDebtCaseTestData(creditorUsername, id, debtorName, debtorSurname, debtorEmail,
                        debtorPhoneNumber, typeToMatch, DebtCaseStatus.NEW, dueDate, lateInterestRate, debtInterestRate,
                        amountOwed, debtorUsername));
        when(debtCaseRepository.findByDueDateLessThanEqual(currentDate)).thenReturn(Optional.of(expectedDebtCases));

        scheduler.calculateOutstandingBalanceScheduler();

        verify(debtCaseRepository, times(expectedDebtCases.size())).save(debtCaseCaptor.capture());
        List<DebtCase> capturedDebtCases = debtCaseCaptor.getAllValues();
        Assertions.assertEquals(amountOwed.multiply(BigDecimal.valueOf(lateInterestRate / 100.0)).add(amountOwed),
                capturedDebtCases.get(0).getAmountOwed());
    }

    @Test
    void calculateOutstandingBalanceWithInterestRateScheduler_EachMonth_ShouldCalculateOutstandingBalanceForEachCase() {
        String debtorUsername = "debtor";
        String debtorName = "name";
        String debtorSurname = "surname";
        String debtorEmail = "email@gmail.com";
        String debtorPhoneNumber = "+37067144213";
        String creditorUsername = "creditor123";
        String typeToMatch = "DEFAULT_DEBT";
        LocalDateTime currentDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        LocalDateTime dueDate =
                LocalDateTime.parse(LocalDateTime.now().plusDays(1).format(Constants.DATE_TIME_FORMATTER),
                        Constants.DATE_TIME_FORMATTER);
        double lateInterestRate = 10.0;
        double debtInterestRate = 10.0;
        BigDecimal amountOwed = BigDecimal.valueOf(35.53);
        int id = 1;
        List<DebtCase> expectedDebtCases =
                List.of(TestUtils.setupDebtCaseTestData(creditorUsername, id, debtorName, debtorSurname, debtorEmail,
                        debtorPhoneNumber, typeToMatch, DebtCaseStatus.NEW, dueDate, lateInterestRate, debtInterestRate,
                        amountOwed, debtorUsername));
        when(debtCaseRepository.findAll()).thenReturn((expectedDebtCases));

        scheduler.calculateOutstandingBalanceWithInterestRateScheduler();

        verify(debtCaseRepository, times(expectedDebtCases.size())).save(debtCaseCaptor.capture());
        List<DebtCase> capturedDebtCases = debtCaseCaptor.getAllValues();
        Assertions.assertEquals(amountOwed.multiply(BigDecimal.valueOf((debtInterestRate / 12) / 100.0)).add(amountOwed),
                capturedDebtCases.get(0).getAmountOwed());
    }
}
