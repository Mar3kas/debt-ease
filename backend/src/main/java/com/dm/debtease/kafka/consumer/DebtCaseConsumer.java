package com.dm.debtease.kafka.consumer;

import com.dm.debtease.model.DebtCase;
import com.dm.debtease.repository.DebtCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DebtCaseConsumer {
    private final DebtCaseRepository debtCaseRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "base-debt-case-topic")
    public void consumeAndEnrich(DebtCase debtCase) {
        // Implement enrichment logic
        DebtCase enrichedDebtCase = new DebtCase();
        // Store the enriched debt case in the database
        debtCaseRepository.save(debtCase);
        String username = debtCase.getCreditor().getUser().getUsername();
        messagingTemplate.convertAndSendToUser(username, "/topic/enriched-debt-cases", debtCase);
    }
}
