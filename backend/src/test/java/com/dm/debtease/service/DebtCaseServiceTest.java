package com.dm.debtease.service;

import com.dm.debtease.TestUtils;
import com.dm.debtease.model.DebtCase;
import com.dm.debtease.model.DebtCaseStatus;
import com.dm.debtease.model.DebtCaseType;
import com.dm.debtease.model.dto.DebtCaseDTO;
import com.dm.debtease.model.dto.PaymentRequestDTO;
import com.dm.debtease.repository.DebtCaseRepository;
import com.dm.debtease.service.impl.DebtCaseServiceImpl;
import com.dm.debtease.utils.Constants;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unused")
public class DebtCaseServiceTest {
    @Mock
    DebtCaseTypeService debtCaseTypeService;
    @Mock
    DebtCaseRepository debtCaseRepository;
    @InjectMocks
    DebtCaseServiceImpl debtCaseService;

    @Test
    void getAllDebtCases_WhenDebtCasesExist_ShouldReturnNonEmptyList() {
        when(debtCaseRepository.findAll()).thenReturn(List.of(new DebtCase()));

        List<DebtCase> actualDebtCases = debtCaseService.getAllDebtCases();

        Assertions.assertNotNull(actualDebtCases);
        Assertions.assertFalse(actualDebtCases.isEmpty());
    }

    @Test
    void getDebtCaseById_WhenDebtCaseExists_ShouldReturnCorrectDebtCase() {
        int creditorId = 1;
        int id = 1;
        String debtorUsername = "userWithDebts";
        String debtorName = "name";
        String debtorSurname = "surname";
        String debtorEmail = "email@gmail.com";
        String debtorPhoneNumber = "+37067144213";
        String creditorUsername = "creditor123";
        String typeToMatch = "DEFAULT_DEBT";
        LocalDateTime dueDate = LocalDateTime.parse(LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER),
                Constants.DATE_TIME_FORMATTER);
        double lateInterestRate = 10.0;
        double debtInterestRate = 10.0;
        BigDecimal amountOwed = BigDecimal.valueOf(35.53);
        DebtCase expectedDebtCase =
                TestUtils.setupDebtCaseTestData(creditorUsername, creditorId, debtorName, debtorSurname, debtorEmail,
                        debtorPhoneNumber, typeToMatch, DebtCaseStatus.NEW, dueDate, lateInterestRate, debtInterestRate,
                        amountOwed, debtorUsername);
        when(debtCaseRepository.findById(id)).thenReturn(Optional.of(expectedDebtCase));

        DebtCase actualDebtCase = debtCaseService.getDebtCaseById(id);

        Assertions.assertNotNull(actualDebtCase);
        Assertions.assertEquals(expectedDebtCase.getDebtor().getName(), actualDebtCase.getDebtor().getName());
        Assertions.assertEquals(expectedDebtCase.getAmountOwed(), actualDebtCase.getAmountOwed());
        Assertions.assertEquals(expectedDebtCase.getLateInterestRate(), actualDebtCase.getLateInterestRate());
        Assertions.assertEquals(expectedDebtCase.getDebtCaseType().getType(),
                actualDebtCase.getDebtCaseType().getType());
        Assertions.assertEquals(expectedDebtCase.getDebtCaseStatus(),
                actualDebtCase.getDebtCaseStatus());
        Assertions.assertEquals(expectedDebtCase.getDueDate(), actualDebtCase.getDueDate());
    }

    @Test
    void getDebtCaseById_WhenDebtCaseDoesNotExist_ShouldThrowException() {
        int id = -1;
        when(debtCaseRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> debtCaseService.getDebtCaseById(id),
                "Expected getDebtCaseById to throw, but it didn't"
        );

        Assertions.assertTrue(thrown.getMessage().contains(String.format(Constants.DEBT_CASE_NOT_FOUND, id)));
    }

    @Test
    void getDebtCasesByCreditorUsername_WhenDebtCasesExist_ShouldReturnDebtCases() {
        int creditorId = 1;
        int id = 1;
        String debtorUsername = "userWithDebts";
        String debtorName = "name";
        String debtorSurname = "surname";
        String debtorEmail = "email@gmail.com";
        String debtorPhoneNumber = "+37067144213";
        String creditorUsername = "creditor123";
        String typeToMatch = "DEFAULT_DEBT";
        LocalDateTime dueDate = LocalDateTime.parse(LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER),
                Constants.DATE_TIME_FORMATTER);
        double lateInterestRate = 10.0;
        double debtInterestRate = 10.0;
        BigDecimal amountOwed = BigDecimal.valueOf(35.53);
        DebtCase expectedDebtCase =
                TestUtils.setupDebtCaseTestData(creditorUsername, creditorId, debtorName, debtorSurname, debtorEmail,
                        debtorPhoneNumber, typeToMatch, DebtCaseStatus.NEW, dueDate, lateInterestRate, debtInterestRate,
                        amountOwed, debtorUsername);
        when(debtCaseRepository.findByCreditor_User_Username(creditorUsername)).thenReturn(List.of(expectedDebtCase));

        List<DebtCase> actualDebtCases = debtCaseService.getDebtCasesByCreditorUsername(creditorUsername);

        Assertions.assertNotNull(actualDebtCases);
        for (DebtCase debtCase : actualDebtCases) {
            Assertions.assertEquals(expectedDebtCase.getCreditor().getUser().getUsername(),
                    debtCase.getCreditor().getUser().getUsername());
        }
    }

    @Test
    void getDebtCasesByDebtorUsername_WhenDebtCasesExist_ShouldReturnDebtCases() {
        int creditorId = 1;
        int id = 1;
        String debtorUsername = "userWithDebts";
        String debtorName = "name";
        String debtorSurname = "surname";
        String debtorEmail = "email@gmail.com";
        String debtorPhoneNumber = "+37067144213";
        String creditorUsername = "creditor123";
        String typeToMatch = "DEFAULT_DEBT";
        LocalDateTime dueDate = LocalDateTime.parse(LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER),
                Constants.DATE_TIME_FORMATTER);
        double lateInterestRate = 10.0;
        double debtInterestRate = 10.0;
        BigDecimal amountOwed = BigDecimal.valueOf(35.53);
        DebtCase expectedDebtCase =
                TestUtils.setupDebtCaseTestData(creditorUsername, creditorId, debtorName, debtorSurname, debtorEmail,
                        debtorPhoneNumber, typeToMatch, DebtCaseStatus.NEW, dueDate, lateInterestRate, debtInterestRate,
                        amountOwed, debtorUsername);
        when(debtCaseRepository.findByDebtor_User_Username(debtorUsername)).thenReturn(List.of(expectedDebtCase));

        List<DebtCase> actualDebtCases = debtCaseService.getDebtCasesByDebtorUsername(debtorUsername);

        Assertions.assertNotNull(actualDebtCases);
        for (DebtCase debtCase : actualDebtCases) {
            Assertions.assertEquals(expectedDebtCase.getDebtor().getUser().getUsername(),
                    debtCase.getDebtor().getUser().getUsername());
        }
    }

    @Test
    void editDebtCaseByIdAndCreditorId_WhenDebtCaseExists_ShouldReturnEditedDebtCase() {
        LocalDateTime editedDueDate = LocalDateTime.now();
        int typeId = 1;
        int creditorId = 1;
        int id = 1;
        String debtorUsername = "userWithDebts";
        String debtorName = "name";
        String debtorSurname = "surname";
        String debtorEmail = "email@gmail.com";
        String debtorPhoneNumber = "+37067144213";
        String creditorUsername = "creditor123";
        String typeToMatch = "DEFAULT_DEBT";
        LocalDateTime dueDate = LocalDateTime.parse(LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER),
                Constants.DATE_TIME_FORMATTER);
        double lateInterestRate = 10.0;
        double debtInterestRate = 10.0;
        BigDecimal amountOwed = BigDecimal.valueOf(35.53);
        DebtCaseDTO debtCaseDTO = TestUtils.setupDebtCaseDTOTestData(editedDueDate, typeId);
        DebtCase expectedDebtCase =
                TestUtils.setupDebtCaseTestData(creditorUsername, creditorId, debtorName, debtorSurname, debtorEmail,
                        debtorPhoneNumber, typeToMatch, DebtCaseStatus.NEW, dueDate, lateInterestRate, debtInterestRate,
                        amountOwed, debtorUsername);
        DebtCaseType expectedDebtCaseType = TestUtils.setupDebtCaseTypeTestData(typeToMatch);
        when(debtCaseRepository.findByIdAndCreditor_Id(id, creditorId)).thenReturn(Optional.of(new DebtCase()));
        when(debtCaseTypeService.getDebtCaseTypeById(typeId)).thenReturn(expectedDebtCaseType);
        when(debtCaseRepository.save(any(DebtCase.class))).thenReturn(expectedDebtCase);

        DebtCase actualEditedDebtCase = debtCaseService.editDebtCaseByIdAndCreditorId(debtCaseDTO, id, creditorId);

        Assertions.assertEquals(expectedDebtCase.getDueDate(), actualEditedDebtCase.getDueDate());
        Assertions.assertEquals(expectedDebtCase.getDebtCaseType().getType(),
                actualEditedDebtCase.getDebtCaseType().getType());
    }

    @Test
    void editDebtCaseByIdAndCreditorId_WhenDebtCaseDoesNotExist_ShouldThrowException() {
        int id = -1;
        int creditorId = 1;
        when(debtCaseRepository.findByIdAndCreditor_Id(id, creditorId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> debtCaseService.editDebtCaseByIdAndCreditorId(new DebtCaseDTO(), id, creditorId),
                "Expected editDebtCaseByIdAndCreditorId to throw, but it didn't"
        );

        Assertions.assertTrue(thrown.getMessage()
                .contains(String.format(Constants.DEBT_CASE_NOT_FOUND_WITH_ID_CREDITOR_ID, id, creditorId)));
    }

    @Test
    void deleteDebtCaseByIdAndCreditorId_WhenDebtCaseExists_ShouldNotThrowException() {
        int id = 1;
        int creditorId = 1;
        when(debtCaseRepository.findByIdAndCreditor_Id(id, creditorId)).thenReturn(Optional.of(new DebtCase()));

        doNothing().when(debtCaseRepository).deleteById(id);

        Assertions.assertDoesNotThrow(() -> debtCaseService.deleteDebtCaseByIdAndCreditorId(id, creditorId));
    }

    @Test
    void deleteDebtCaseByIdAndCreditorId_WhenDebtCaseDoesNotExist_ShouldThrowException() {
        int id = -1;
        int creditorId = 1;
        when(debtCaseRepository.findByIdAndCreditor_Id(id, creditorId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> debtCaseService.deleteDebtCaseByIdAndCreditorId(id, creditorId),
                "Expected deleteDebtCaseByIdAndCreditorId to throw, but it didn't"
        );

        Assertions.assertTrue(thrown.getMessage().contains(String.format(Constants.DEBT_CASE_NOT_FOUND, id)));
    }

    @Test
    void findExistingDebtCase_WhenDebtCaseExists_ShouldReturnOptionalContainingDebtCase() {
        BigDecimal amountOwed = new BigDecimal(TestUtils.INDICATOR[0]);
        LocalDateTime dueDate = LocalDateTime.parse(TestUtils.INDICATOR[1], Constants.DATE_TIME_FORMATTER);
        String debtCaseType = debtCaseService.getTypeToMatch(TestUtils.INDICATOR[2]);
        String username = "username";
        String debtorName = TestUtils.INDICATOR[3];
        String debtorSurname = TestUtils.INDICATOR[4];
        DebtCase expectedDebtCase = new DebtCase();
        when(debtCaseRepository.findByAmountOwedAndDueDateAndDebtCaseType_TypeAndCreditor_User_UsernameAndDebtor_NameAndDebtor_Surname(
                amountOwed, dueDate, debtCaseType, username, debtorName, debtorSurname))
                .thenReturn(Optional.of(expectedDebtCase));

        Optional<DebtCase> optionalActualDebtCase = debtCaseService.findExistingDebtCase(username, TestUtils.INDICATOR);

        Assertions.assertTrue(optionalActualDebtCase.isPresent());
        Assertions.assertEquals(expectedDebtCase, optionalActualDebtCase.get());
        verify(debtCaseRepository,
                times(1)).findByAmountOwedAndDueDateAndDebtCaseType_TypeAndCreditor_User_UsernameAndDebtor_NameAndDebtor_Surname(
                amountOwed, dueDate, debtCaseType, username, debtorName, debtorSurname);
    }

    @Test
    void getTypeToMatch_WhenTypeExists_ShouldReturnMatchedType() {
        String input = "personal_debt";
        String expected = "PERSONAL_DEBT";

        String actual = debtCaseService.getTypeToMatch(input);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getTypeToMatch_WhenTypeExists_NoDebtWord_ShouldReturnMatchedType() {
        String input = "personal";
        String expected = "PERSONAL_DEBT";

        String actual = debtCaseService.getTypeToMatch(input);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getTypeToMatch_WhenTypeDoesNotExist_ShouldReturnDefaultType() {
        String input = "";
        String expected = "DEFAULT_DEBT";

        String actual = debtCaseService.getTypeToMatch(input);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void isDebtCasePending_WhenWithinRangeAndStatusNew_ShouldReturnTrue() {
        LocalDateTime dueDate = LocalDateTime.of(2024, 3, 22, 12, 0);
        LocalDateTime startTime = LocalDateTime.of(2024, 3, 20, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 3, 25, 23, 59);
        DebtCase debtCase = new DebtCase();
        debtCase.setDebtCaseStatus(DebtCaseStatus.NEW);
        debtCase.setDueDate(dueDate);

        Assertions.assertTrue(debtCaseService.isDebtCasePending(debtCase, startTime, endTime));
    }

    @Test
    void isDebtCasePending_WhenOutOfRangeBeforeDueDate_ShouldReturnFalse() {
        LocalDateTime dueDate = LocalDateTime.of(2024, 3, 19, 23, 59);
        LocalDateTime startTime = LocalDateTime.of(2024, 3, 20, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 3, 25, 23, 59);
        DebtCase debtCase = new DebtCase();
        debtCase.setDebtCaseStatus(DebtCaseStatus.NEW);
        debtCase.setDueDate(dueDate);

        Assertions.assertFalse(debtCaseService.isDebtCasePending(debtCase, startTime, endTime));
    }

    @Test
    void isDebtCasePending_WhenOutOfRangeAfterDueDate_ShouldReturnFalse() {
        LocalDateTime dueDate = LocalDateTime.of(2024, 3, 26, 0, 1);
        LocalDateTime startTime = LocalDateTime.of(2024, 3, 20, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 3, 25, 23, 59);
        DebtCase debtCase = new DebtCase();
        debtCase.setDebtCaseStatus(DebtCaseStatus.NEW);
        debtCase.setDueDate(dueDate);

        Assertions.assertFalse(debtCaseService.isDebtCasePending(debtCase, startTime, endTime));
    }

    @Test
    void isDebtCasePending_WhenWithinRangeButStatusNotNew_ShouldReturnFalse() {
        LocalDateTime dueDate = LocalDateTime.of(2024, 3, 22, 12, 0);
        LocalDateTime startTime = LocalDateTime.of(2024, 3, 20, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 3, 25, 23, 59);
        DebtCase debtCase = new DebtCase();
        debtCase.setDebtCaseStatus(DebtCaseStatus.CLOSED);
        debtCase.setDueDate(dueDate);

        Assertions.assertFalse(debtCaseService.isDebtCasePending(debtCase, startTime, endTime));
    }

    @Test
    void updateDebtCaseAfterPayment_PaymentIsInFull_StatusShouldBeClosed_AmountOwedZero() {
        int creditorId = 1;
        int id = 1;
        String debtorUsername = "userWithDebts";
        String debtorName = "name";
        String debtorSurname = "surname";
        String debtorEmail = "email@gmail.com";
        String debtorPhoneNumber = "+37067144213";
        String creditorUsername = "creditor123";
        String typeToMatch = "DEFAULT_DEBT";
        LocalDateTime dueDate = LocalDateTime.parse(LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER),
                Constants.DATE_TIME_FORMATTER);
        double lateInterestRate = 10.0;
        double debtInterestRate = 10.0;
        BigDecimal amountOwed = BigDecimal.valueOf(35.53);
        DebtCase expectedDebtCase =
                TestUtils.setupDebtCaseTestData(creditorUsername, creditorId, debtorName, debtorSurname, debtorEmail,
                        debtorPhoneNumber, typeToMatch, DebtCaseStatus.NEW, dueDate, lateInterestRate, debtInterestRate,
                        amountOwed, debtorUsername);
        String sourceId = "random-id";
        boolean isPaymentInFull = true;
        PaymentRequestDTO paymentRequestDTO =
                TestUtils.setupPaymentRequestDTOTestData(sourceId, amountOwed, isPaymentInFull);

        when(debtCaseRepository.save(any(DebtCase.class))).thenReturn(expectedDebtCase);

        DebtCase actualDebtCase = debtCaseService.updateDebtCaseAfterPayment(expectedDebtCase, paymentRequestDTO);

        Assertions.assertNotNull(actualDebtCase);
        Assertions.assertEquals(BigDecimal.ZERO, actualDebtCase.getAmountOwed());
        Assertions.assertEquals(DebtCaseStatus.CLOSED, actualDebtCase.getDebtCaseStatus());
    }

    @Test
    void updateDebtCaseAfterPayment_PaymentIsNotInFull_StatusShouldBeUnpaid_AmountOwedSubtracted() {
        int creditorId = 1;
        int id = 1;
        String debtorUsername = "userWithDebts";
        String debtorName = "name";
        String debtorSurname = "surname";
        String debtorEmail = "email@gmail.com";
        String debtorPhoneNumber = "+37067144213";
        String creditorUsername = "creditor123";
        String typeToMatch = "DEFAULT_DEBT";
        LocalDateTime dueDate = LocalDateTime.parse(LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER),
                Constants.DATE_TIME_FORMATTER);
        double lateInterestRate = 10.0;
        double debtInterestRate = 10.0;
        BigDecimal amountOwed = BigDecimal.valueOf(35.53);
        DebtCase expectedDebtCase =
                TestUtils.setupDebtCaseTestData(creditorUsername, creditorId, debtorName, debtorSurname, debtorEmail,
                        debtorPhoneNumber, typeToMatch, DebtCaseStatus.NEW, dueDate, lateInterestRate, debtInterestRate,
                        amountOwed, debtorUsername);
        String sourceId = "random-id";
        boolean isPaymentInFull = false;
        PaymentRequestDTO paymentRequestDTO =
                TestUtils.setupPaymentRequestDTOTestData(sourceId, BigDecimal.TEN, isPaymentInFull);
        when(debtCaseRepository.save(any(DebtCase.class))).thenReturn(expectedDebtCase);

        DebtCase actualDebtCase = debtCaseService.updateDebtCaseAfterPayment(expectedDebtCase, paymentRequestDTO);

        Assertions.assertNotNull(actualDebtCase);
        Assertions.assertEquals(amountOwed.subtract(BigDecimal.TEN), actualDebtCase.getAmountOwed());
        Assertions.assertEquals(DebtCaseStatus.UNPAID, actualDebtCase.getDebtCaseStatus());
    }

    @Test
    void getValidLeftAmountOwed_PaymentAmountIsGreater_ShouldReturnCurrentAmountOwed() {
        BigDecimal paymentAmount = BigDecimal.valueOf(25.25);
        BigDecimal currentAmountOwed = BigDecimal.valueOf(10);

        BigDecimal actualAmountOwed = debtCaseService.getValidLeftAmountOwed(paymentAmount, currentAmountOwed);

        Assertions.assertEquals(currentAmountOwed, actualAmountOwed);
    }

    @Test
    void getValidLeftAmountOwed_PaymentAmountIsEqual_ShouldReturnZero() {
        BigDecimal paymentAmount = BigDecimal.valueOf(25.25);
        BigDecimal currentAmountOwed = BigDecimal.valueOf(25.25);

        BigDecimal actualAmountOwed = debtCaseService.getValidLeftAmountOwed(paymentAmount, currentAmountOwed);

        Assertions.assertEquals(currentAmountOwed.subtract(paymentAmount), actualAmountOwed);
    }

    @Test
    void getValidLeftAmountOwed_PaymentAmountIsLess_ShouldReturnSubtraction() {
        BigDecimal paymentAmount = BigDecimal.valueOf(10.25);
        BigDecimal currentAmountOwed = BigDecimal.valueOf(25.25);

        BigDecimal actualAmountOwed = debtCaseService.getValidLeftAmountOwed(paymentAmount, currentAmountOwed);

        Assertions.assertEquals(currentAmountOwed.subtract(paymentAmount), actualAmountOwed);
    }
}
