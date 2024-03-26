package com.dm.debtease.service;

import com.dm.debtease.TestUtils;
import com.dm.debtease.model.DebtCaseStatus;
import com.dm.debtease.repository.DebtCaseStatusRepository;
import com.dm.debtease.service.impl.DebtCaseStatusServiceImpl;
import com.dm.debtease.utils.Constants;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DebtCaseStatusServiceTest {
    @Mock
    private DebtCaseStatusRepository debtCaseStatusRepository;
    @InjectMocks
    private DebtCaseStatusServiceImpl debtCaseStatusService;

    @Test
    void getDebtCaseStatusById_WhenStatusExists_ShouldReturnDebtCaseStatus() {
        int id = 1;
        String status = "status";
        DebtCaseStatus expectedDebtCaseStatus = TestUtils.setupDebtCaseStatusTestData(status);
        when(debtCaseStatusRepository.findById(id)).thenReturn(Optional.of(expectedDebtCaseStatus));

        DebtCaseStatus actualDebtCaseStatus = debtCaseStatusService.getDebtCaseStatusById(id);

        Assertions.assertNotNull(actualDebtCaseStatus);
        Assertions.assertEquals(expectedDebtCaseStatus.getStatus(), actualDebtCaseStatus.getStatus());
    }

    @Test
    void getDebtCaseStatusById_WhenStatusDoesNotExist_ShouldThrowException() {
        int id = -1;
        when(debtCaseStatusRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> debtCaseStatusService.getDebtCaseStatusById(id),
                "Expected getDebtCaseStatusById to throw, but it didn't"
        );

        Assertions.assertTrue(thrown.getMessage().contains(String.format(Constants.DEBT_CASE_STATUS_NOT_FOUND, id)));
    }
}
