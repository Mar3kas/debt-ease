package com.dm.debtease.scheduler;

import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.Debtor;
import com.dm.debtease.service.DebtCaseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Log4j2
public class Scheduler {
    private final DebtCaseService debtCaseService;
    private final JavaMailSender javaMailSender;

    @Autowired
    public Scheduler(DebtCaseService debtCaseService, JavaMailSender javaMailSender) {
        this.debtCaseService = debtCaseService;
        this.javaMailSender = javaMailSender;
    }

    @Scheduled(cron = "0 * * * * *")
    public void emailNotificationScheduler() {
        log.info("Starting cron job scheduler for email notification!");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenDaysLater = now.plusDays(10);

        List<DebtCase> debtCases = debtCaseService.getAllDebtCases();

        for (DebtCase debtCase : debtCases) {
            if (debtCaseIsPending(debtCase, now, tenDaysLater)) {
                sendNotificationEmail(debtCase);
            }
        }

        log.info("Cron job scheduler for email notification has finished!");
    }

    private boolean debtCaseIsPending(DebtCase debtCase, LocalDateTime startTime, LocalDateTime endTime) {
        return debtCase.getDueDate().isAfter(startTime)
                && debtCase.getDueDate().isBefore(endTime)
                && "NEW" .equals(debtCase.getDebtCaseType().getType())
                && debtCase.getIsSent() != 1;
    }

    private void sendNotificationEmail(DebtCase debtCase) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("marijuspet@gmail.com");
        Debtor debtor = debtCase.getDebtors().stream().
                filter(debtor1 -> debtor1.getDebtCase().getId() == debtCase.getId())
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Debtor not found that has this debtcase with this id " + debtCase.getId()));

        mailMessage.setTo(debtor.getEmail());
        mailMessage.setSubject("Pending debt until " + debtCase.getDueDate());
        mailMessage.setText(String.format("Dear, %s %s! %n You have an open debt case with an outstanding amount: %f.2f! Please pay by %s!",
                debtor.getName(), debtor.getSurname(),
                debtCase.getAmountOwed(), debtCase.getDueDate()));

        javaMailSender.send(mailMessage);
        debtCaseService.markDebtCaseEmailAsSentById(debtCase.getId());
    }
}