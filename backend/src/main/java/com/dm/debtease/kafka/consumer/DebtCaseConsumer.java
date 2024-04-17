package com.dm.debtease.kafka.consumer;

import com.dm.debtease.model.Company;
import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.VerifiedPhoneNumberInformation;
import com.dm.debtease.repository.*;
import com.dm.debtease.service.EmailService;
import com.dm.debtease.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
@SuppressWarnings("unused")
public class DebtCaseConsumer {
    private final DebtCaseRepository debtCaseRepository;
    private final CreditorRepository creditorRepository;
    private final DebtorRepository debtorRepository;
    private final CompanyRepository companyRepository;
    private final VerifiedPhoneNumberInformationRepository verifiedPhoneNumberInformationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final EmailService emailService;

    @Value("${spring.numverify.api.access-key}")
    private String numverifyAccessKey;
    private static final Map<String, String> PHONE_FORMAT_MAP = Map.of(
            "mobile", "Mobile Phone",
            "landline", "Landline",
            "special_services", "Special Services",
            "toll_free", "Toll-Free Numbers",
            "premium_rate", "Premium Rate Numbers",
            "satellite", "Satellite",
            "paging", "Paging"
    );

    @KafkaListener(topics = "base-debt-case-topic")
    public void consumeAndEnrich(DebtCase debtCase) {
        log.info(String.format("Consuming %s", debtCase));
        DebtCase enrichedDebtCase = debtCaseRepository.save(enrich(debtCase));
        emailService.sendNotificationEmail(enrichedDebtCase);
        messagingTemplate.convertAndSendToUser(
                enrichedDebtCase.getCreditor().getUser().getUsername(),
                "/topic/enriched-debt-cases",
                enrichedDebtCase
        );
    }

    private DebtCase enrich(DebtCase debtCase) {
        Company company = companyRepository.findByNameContainingIgnoreCase(debtCase.getCreditor().getName());
        if (debtCase.getCreditor().getCompany() == null || !debtCase.getCreditor().getCompany().equals(company))
        {
            debtCase.getCreditor().setCompany(company);
        }
        String phoneNumber = fixPhoneNumberFormat(debtCase.getDebtor().getPhoneNumber());
        return validatePhoneNumber(phoneNumber, debtCase);
    }

    private DebtCase validatePhoneNumber(String phoneNumber, DebtCase debtCase) {
        try {
            delayBetweenRequests();
            JSONObject jsonObject = getJsonObjectFromNumverify(phoneNumber);
            VerifiedPhoneNumberInformation verifiedPhoneNumberInformation = new VerifiedPhoneNumberInformation();
            verifiedPhoneNumberInformation.setValid(jsonObject.getBoolean("valid") ? "Yes" : "No");
            verifiedPhoneNumberInformation.setLocation(jsonObject.getString("location"));
            verifiedPhoneNumberInformation.setCarrier(jsonObject.getString("carrier"));
            verifiedPhoneNumberInformation.setLineType(PHONE_FORMAT_MAP.get(jsonObject.getString("line_type")));
            verifiedPhoneNumberInformationRepository.save(verifiedPhoneNumberInformation);
            debtCase.getDebtor().setVerifiedPhoneNumberInformation(verifiedPhoneNumberInformation);
            debtorRepository.save(debtCase.getDebtor());
            return debtCase;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return debtCase;
    }

    private JSONObject getJsonObjectFromNumverify(String phoneNumber) throws IOException {
        String apiUrl = "http://apilayer.net/api/validate?access_key=" + numverifyAccessKey + "&number=" + phoneNumber + "&format=1";
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        connection.disconnect();
        return new JSONObject(response.toString());
    }

    private String fixPhoneNumberFormat(String phoneNumber) {
        if (phoneNumber != null && phoneNumber.startsWith("+")) {
            return phoneNumber.substring(1);
        }
        return phoneNumber;
    }

    private void delayBetweenRequests() {
        try {
            Thread.sleep(Constants.DELAY_BETWEEN_NUMVERIFY_REQUESTS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}