package com.dm.debtease.scheduler;

import com.dm.debtease.model.Creditor;
import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.Debtor;
import com.dm.debtease.repository.DebtCaseRepository;
import com.dm.debtease.service.DebtCaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
    private final JavaMailSender javaMailSender;

    @Scheduled(cron = "0 * * * * *")
    public void emailNotificationForUpcomingDueDatePaymentScheduler() {
        log.info("Starting cron job scheduler for email notification for upcoming due date payment!");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenDaysLater = now.plusDays(10);
        List<DebtCase> debtCases = debtCaseService.getAllDebtCases();
        for (DebtCase debtCase : debtCases) {
            if (debtCaseIsPending(debtCase, now, tenDaysLater)) {
                sendNotificationEmail(debtCase);
            }
        }
        log.info("Cron job scheduler for email notification for upcoming due date payment has finished!");
    }

    @Scheduled(cron = "0 0 0 20 * *")
    public void emailNotificationEachMonth20DayScheduler() {
        log.info("Starting cron job scheduler for email notification each month 20th day!");
        List<DebtCase> debtCases = debtCaseService.getAllDebtCases();
        for (DebtCase debtCase : debtCases) {
            sendNotificationEmail(debtCase);
        }
        log.info("Cron job scheduler for email notification each month 20th day has finished!");
    }

    @Scheduled(cron = "0 * * * * *")
    //@Scheduled(cron = "0 0 23 * * *")
    public void calculateOutstandingBalance() {
        log.info("Starting cron job scheduler for calculating outstanding balance!");
        LocalDateTime currentDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        Optional<List<DebtCase>> optionalDebtCases = debtCaseRepository.findByDueDateLessThanEqual(currentDate);
        if (optionalDebtCases.isPresent()) {
            List<DebtCase> debtCases = optionalDebtCases.get();
            for (DebtCase debtCase : debtCases) {
                BigDecimal lateInterestAmount = debtCase.getAmountOwed().multiply(BigDecimal.valueOf(debtCase.getLateInterestRate() / 100.0));
                BigDecimal updatedOutstandingBalance = debtCase.getOutstandingBalance().add(lateInterestAmount);
                debtCase.setOutstandingBalance(updatedOutstandingBalance);
                debtCaseRepository.save(debtCase);
            }
        }
        log.info("Cron job scheduler for calculating outstanding balance has finished!");
    }

    private boolean debtCaseIsPending(DebtCase debtCase, LocalDateTime startTime, LocalDateTime endTime) {
        return debtCase.getDueDate().isAfter(startTime)
                && debtCase.getDueDate().isBefore(endTime)
                && "NEW".equals(debtCase.getDebtCaseType().getType());
    }

    private void sendNotificationEmail(DebtCase debtCase) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("marijuspet@gmail.com");
        Debtor debtor = debtCase.getDebtor();
        Creditor creditor = debtCase.getCreditor();
        mailMessage.setTo(debtor.getEmail());
        mailMessage.setSubject("Pending debt until " + debtCase.getDueDate());
        mailMessage.setText(String.format("Dear, %s %s! %n You have an open debt case with an amount owed: %f.2f! Issued by %s. Please pay by %s!",
                debtor.getName(), debtor.getSurname(),
                debtCase.getAmountOwed(), creditor.getName(),
                debtCase.getDueDate()));
        javaMailSender.send(mailMessage);
    }
}