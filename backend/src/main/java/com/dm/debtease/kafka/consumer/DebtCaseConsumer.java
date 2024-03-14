package com.dm.debtease.kafka.consumer;

import com.dm.debtease.model.CompanyInformation;
import com.dm.debtease.model.DebtCase;
import com.dm.debtease.repository.CompanyInformationRepository;
import com.dm.debtease.repository.DebtCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DebtCaseConsumer {
    private final DebtCaseRepository debtCaseRepository;
    private final CompanyInformationRepository companyInformationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "base-debt-case-topic")
    public void consumeAndEnrich(DebtCase debtCase) {
        DebtCase enrichedDebtCase = debtCaseRepository.save(enrich(debtCase));
        String username = enrichedDebtCase.getCreditor().getUser().getUsername();
        messagingTemplate.convertAndSendToUser(username, "/topic/enriched-debt-cases", enrichedDebtCase);
    }

    private DebtCase enrich(DebtCase debtCase) {
        CompanyInformation companyInformation = companyInformationRepository.findByNameContainingIgnoreCase(debtCase.getCreditor().getName());
        debtCase.setCompany(companyInformation);
        return debtCase;
    }
}
