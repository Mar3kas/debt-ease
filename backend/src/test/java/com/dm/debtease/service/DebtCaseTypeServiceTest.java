package com.dm.debtease.service;

import com.dm.debtease.TestUtils;
import com.dm.debtease.model.DebtCaseType;
import com.dm.debtease.repository.DebtCaseTypeRepository;
import com.dm.debtease.service.impl.DebtCaseTypeServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
