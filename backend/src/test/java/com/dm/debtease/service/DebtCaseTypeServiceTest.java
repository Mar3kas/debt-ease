package com.dm.debtease.service;

import com.dm.debtease.TestUtils;
import com.dm.debtease.model.DebtCaseType;
import com.dm.debtease.repository.DebtCaseTypeRepository;
import com.dm.debtease.service.impl.DebtCaseTypeServiceImpl;
import com.dm.debtease.utils.Constants;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DebtCaseTypeServiceTest {
    @Mock
    private DebtCaseTypeRepository debtCaseTypeRepository;
    @InjectMocks
    private DebtCaseTypeServiceImpl debtCaseTypeService;

    @Test
    void testGetAllDebtCaseTypes() {
        when(debtCaseTypeRepository.findAll()).thenReturn(List.of(new DebtCaseType()));
        List<DebtCaseType> actualDebtCaseTypes = debtCaseTypeService.getAllDebtCaseTypes();
        Assertions.assertNotNull(actualDebtCaseTypes);
        Assertions.assertFalse(actualDebtCaseTypes.isEmpty());
    }

    @Test
    void testGetDebtCaseTypeById() {
        String typeToMatch = "DEFAULT_DEBT";
        DebtCaseType expectedDebtCaseType = TestUtils.setupDebtCaseTypeTestData(typeToMatch);
        int id = 1;
        when(debtCaseTypeRepository.findById(id)).thenReturn(Optional.of(expectedDebtCaseType));
        DebtCaseType actualDebtCaseType = debtCaseTypeService.getDebtCaseTypeById(id);
        Assertions.assertNotNull(actualDebtCaseType);
        Assertions.assertEquals(expectedDebtCaseType.getType(), actualDebtCaseType.getType());
    }

    @Test
    void testGetDebtCaseTypeByNonExistingId() {
        int id = -1;
        when(debtCaseTypeRepository.findById(id)).thenReturn(Optional.empty());
        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> debtCaseTypeService.getDebtCaseTypeById(id),
                "Expected getDebtCaseTypeById to throw, but it didn't"
        );
        Assertions.assertTrue(thrown.getMessage().contains(String.format(Constants.DEBT_CASE_TYPE_NOT_FOUND, id)));
    }

    @Test
    void testGetMatchingDebtCaseTypeByType() {
        String typeToMatch = "DEFAULT_DEBT";
        DebtCaseType expectedDebtCaseType = TestUtils.setupDebtCaseTypeTestData(typeToMatch);
        when(debtCaseTypeRepository.findAll()).thenReturn(List.of(expectedDebtCaseType));
        DebtCaseType actualMatchedDebtCaseType = debtCaseTypeService.findMatchingDebtCaseType(typeToMatch);
        Assertions.assertNotNull(actualMatchedDebtCaseType);
        Assertions.assertEquals(expectedDebtCaseType.getType(), actualMatchedDebtCaseType.getType());
    }

    @Test
    void testGetMatchingDebtCaseTypeByInvalidType() {
        String nonExistingDebtType = "NON_EXISTING_DEBT";
        String debtCaseType = "DEFAULT_DEBT";
        DebtCaseType expectedDebtCaseType = TestUtils.setupDebtCaseTypeTestData(debtCaseType);
        when(debtCaseTypeRepository.findAll()).thenReturn(List.of(expectedDebtCaseType));
        DebtCaseType actualMatchedDebtCaseType = debtCaseTypeService.findMatchingDebtCaseType(nonExistingDebtType);
        Assertions.assertNull(actualMatchedDebtCaseType);
    }

    @Test
    void testGetDefaultDebtCaseType() {
        String typeToMatch = "DEFAULT_DEBT";
        DebtCaseType expectedDebtCaseType = TestUtils.setupDebtCaseTypeTestData(typeToMatch);
        when(debtCaseTypeRepository.findAll()).thenReturn(List.of(expectedDebtCaseType));
        DebtCaseType actualMatchedDebtCaseType = debtCaseTypeService.getDefaultDebtCaseType();
        Assertions.assertNotNull(actualMatchedDebtCaseType);
        Assertions.assertEquals(expectedDebtCaseType.getType(), actualMatchedDebtCaseType.getType());
    }

    @Test
    void testGetMissingDefaultDebtCaseType() {
        when(debtCaseTypeRepository.findAll()).thenReturn(List.of());
        DebtCaseType actualMatchedDebtCaseType = debtCaseTypeService.getDefaultDebtCaseType();
        Assertions.assertNull(actualMatchedDebtCaseType);
    }
}
