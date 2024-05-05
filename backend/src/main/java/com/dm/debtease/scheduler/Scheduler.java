package com.dm.debtease.scheduler;

import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.DebtCaseStatus;
import com.dm.debtease.repository.DebtCaseRepository;
import com.dm.debtease.service.DebtCaseService;
import com.dm.debtease.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Log4j2
@SuppressWarnings("unused")
public class Scheduler {
    private final DebtCaseService debtCaseService;
    private final DebtCaseRepository debtCaseRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 12 * * *")
    public void emailNotificationForUpcomingDueDatePaymentScheduler() {
        log.info("Starting cron job scheduler for email notification for upcoming due date payment!");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenDaysLater = now.plusDays(10);
        List<DebtCase> debtCases = debtCaseService.getAllDebtCases();
        for (DebtCase debtCase : debtCases) {
            if (debtCaseService.isDebtCasePending(debtCase, now, tenDaysLater)) {
                emailService.sendNotificationEmail(debtCase);
            }
        }
        log.info("Cron job scheduler for email notification for upcoming due date payment has finished!");
    }

    @Scheduled(cron = "0 0 12 20 * *")
    public void emailNotificationEachMonth20DayScheduler() {
        log.info("Starting cron job scheduler for email notification each month 20th day!");
        List<DebtCase> debtCases = debtCaseService.getAllDebtCases();
        for (DebtCase debtCase : debtCases) {
            if (!DebtCaseStatus.CLOSED.equals(debtCase.getDebtCaseStatus())) {
                emailService.sendNotificationEmail(debtCase);
            }
        }
        log.info("Cron job scheduler for email notification each month 20th day has finished!");
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void calculateOutstandingBalanceScheduler() {
        log.info("Starting cron job scheduler for calculating outstanding balance!");
        LocalDateTime currentDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        Optional<List<DebtCase>> optionalDebtCases = debtCaseRepository.findByDueDateLessThanEqual(currentDate);
        if (optionalDebtCases.isPresent()) {
            List<DebtCase> debtCases = optionalDebtCases.get();
            for (DebtCase debtCase : debtCases) {
                if (!DebtCaseStatus.CLOSED.equals(debtCase.getDebtCaseStatus())) {
                    BigDecimal lateInterestRateAmount =
                            debtCase.getAmountOwed()
                                    .multiply(BigDecimal.valueOf(debtCase.getLateInterestRate() / 100.0));
                    BigDecimal updatedAmountOwedBalance = debtCase.getAmountOwed().add(lateInterestRateAmount);
                    debtCase.setAmountOwed(updatedAmountOwedBalance);
                    debtCaseRepository.save(debtCase);
                }
            }
        }
        log.info("Cron job scheduler for calculating outstanding balance has finished!");
    }

    @Scheduled(cron = "0 0 0 1 * *")
    public void calculateOutstandingBalanceWithInterestRateScheduler() {
        log.info("Starting cron job scheduler for calculating outstanding balance with interest rate!");
        List<DebtCase> debtCases = debtCaseRepository.findAll();
            for (DebtCase debtCase : debtCases) {
                if (!DebtCaseStatus.CLOSED.equals(debtCase.getDebtCaseStatus())) {
                    BigDecimal interestRateAmount =
                            debtCase.getAmountOwed()
                                    .multiply(BigDecimal.valueOf((debtCase.getDebtInterestRate() / 12) / 100));
                    BigDecimal updatedAmountOwed = debtCase.getAmountOwed().add(interestRateAmount);
                    debtCase.setAmountOwed(updatedAmountOwed);
                    debtCaseRepository.save(debtCase);
                }
            }
        log.info("Cron job scheduler for calculating outstanding balance with interest rate has finished!");
    }
}