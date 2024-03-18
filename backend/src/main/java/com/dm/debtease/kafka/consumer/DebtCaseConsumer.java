package com.dm.debtease.kafka.consumer;

import com.dm.debtease.model.CompanyInformation;
import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.VerifiedPhoneNumberInformation;
import com.dm.debtease.repository.CompanyInformationRepository;
import com.dm.debtease.repository.DebtCaseRepository;
import com.dm.debtease.repository.DebtorRepository;
import com.dm.debtease.repository.VerifiedPhoneNumberInformationRepository;
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
public class DebtCaseConsumer {
    private final DebtCaseRepository debtCaseRepository;
    private final DebtorRepository debtorRepository;
    private final VerifiedPhoneNumberInformationRepository verifiedPhoneNumberInformationRepository;
    private final CompanyInformationRepository companyInformationRepository;
    private final SimpMessagingTemplate messagingTemplate;

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
        DebtCase enrichedDebtCase = debtCaseRepository.save(enrich(debtCase));
        messagingTemplate.convertAndSendToUser(
                enrichedDebtCase.getCreditor().getUser().getUsername(),
                "/topic/enriched-debt-cases",
                enrichedDebtCase
        );
    }

    private DebtCase enrich(DebtCase debtCase) {
        CompanyInformation companyInformation = companyInformationRepository.findByNameContainingIgnoreCase(debtCase.getCreditor().getName());
        debtCase.setCompany(companyInformation);
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
            verifiedPhoneNumberInformation = verifiedPhoneNumberInformationRepository.save(verifiedPhoneNumberInformation);
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
            long DELAY_BETWEEN_REQUESTS = 1000;
            Thread.sleep(DELAY_BETWEEN_REQUESTS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}