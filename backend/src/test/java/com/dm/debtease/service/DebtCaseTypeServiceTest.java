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
    void getAllDebtCaseTypes_WhenTypesExist_ShouldReturnListOfTypes() {
        when(debtCaseTypeRepository.findAll()).thenReturn(List.of(new DebtCaseType()));

        List<DebtCaseType> actualDebtCaseTypes = debtCaseTypeService.getAllDebtCaseTypes();

        Assertions.assertNotNull(actualDebtCaseTypes);
        Assertions.assertFalse(actualDebtCaseTypes.isEmpty());
    }

    @Test
    void getDebtCaseTypeById_WhenTypeExists_ShouldReturnDebtCaseType() {
        String typeToMatch = "DEFAULT_DEBT";
        DebtCaseType expectedDebtCaseType = TestUtils.setupDebtCaseTypeTestData(typeToMatch);
        int id = 1;
        when(debtCaseTypeRepository.findById(id)).thenReturn(Optional.of(expectedDebtCaseType));

        DebtCaseType actualDebtCaseType = debtCaseTypeService.getDebtCaseTypeById(id);

        Assertions.assertNotNull(actualDebtCaseType);
        Assertions.assertEquals(expectedDebtCaseType.getType(), actualDebtCaseType.getType());
    }

    @Test
    void getDebtCaseTypeById_WhenTypeDoesNotExist_ShouldThrowException() {
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
    void getMatchingDebtCaseTypeByType_WhenMatchingTypeExists_ShouldReturnMatchingDebtCaseType() {
        String typeToMatch = "DEFAULT_DEBT";
        DebtCaseType expectedDebtCaseType = TestUtils.setupDebtCaseTypeTestData(typeToMatch);
        when(debtCaseTypeRepository.findAll()).thenReturn(List.of(expectedDebtCaseType));

        DebtCaseType actualMatchedDebtCaseType = debtCaseTypeService.findMatchingDebtCaseType(typeToMatch);

        Assertions.assertNotNull(actualMatchedDebtCaseType);
        Assertions.assertEquals(expectedDebtCaseType.getType(), actualMatchedDebtCaseType.getType());
    }

    @Test
    void getMatchingDebtCaseTypeByType_WhenMatchingTypeDoesNotExist_ShouldReturnDefault() {
        String nonExistingDebtType = "NON_EXISTING_DEBT";
        String debtCaseType = "DEFAULT_DEBT";
        DebtCaseType expectedDebtCaseType = TestUtils.setupDebtCaseTypeTestData(debtCaseType);
        when(debtCaseTypeRepository.findAll()).thenReturn(List.of(expectedDebtCaseType));

        DebtCaseType actualMatchedDebtCaseType = debtCaseTypeService.findMatchingDebtCaseType(nonExistingDebtType);

        Assertions.assertNotNull(actualMatchedDebtCaseType);
        Assertions.assertEquals(debtCaseType, actualMatchedDebtCaseType.getType());
    }

    @Test
    void getDefaultDebtCaseType_WhenDefaultTypeExists_ShouldReturnDefaultDebtCaseType() {
        String typeToMatch = "DEFAULT_DEBT";
        DebtCaseType expectedDebtCaseType = TestUtils.setupDebtCaseTypeTestData(typeToMatch);
        when(debtCaseTypeRepository.findAll()).thenReturn(List.of(expectedDebtCaseType));

        DebtCaseType actualMatchedDebtCaseType = debtCaseTypeService.getDefaultDebtCaseType();

        Assertions.assertNotNull(actualMatchedDebtCaseType);
        Assertions.assertEquals(expectedDebtCaseType.getType(), actualMatchedDebtCaseType.getType());
    }

    @Test
    void getDefaultDebtCaseType_WhenDefaultTypeDoesNotExist_ShouldReturnNull() {
        when(debtCaseTypeRepository.findAll()).thenReturn(List.of());

        DebtCaseType actualMatchedDebtCaseType = debtCaseTypeService.getDefaultDebtCaseType();

        Assertions.assertNull(actualMatchedDebtCaseType);
    }

    @Test
    void formatDebtCaseType_ShouldReturnCorrectFormat() {
        String inputDebtCaseType = "TAX_DEBT";
        String expectedDebtCaseType = "Tax Debt";

        String actualDebtCaseType = debtCaseTypeService.formatDebtCaseType(inputDebtCaseType);

        Assertions.assertEquals(expectedDebtCaseType, actualDebtCaseType);
    }

    @Test
    void getTypeToMatch_WhenTypeExists_ShouldReturnMatchedType() {
        String input = "personal_debt";
        String expected = "PERSONAL_DEBT";

        String actual = debtCaseTypeService.getTypeToMatch(input);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getTypeToMatch_WhenTypeExists_NoDebtWord_ShouldReturnMatchedType() {
        String input = "personal";
        String expected = "PERSONAL_DEBT";

        String actual = debtCaseTypeService.getTypeToMatch(input);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getTypeToMatch_WhenTypeDoesNotExist_ShouldReturnDefaultType() {
        String input = "";
        String expected = "DEFAULT_DEBT";

        String actual = debtCaseTypeService.getTypeToMatch(input);

        Assertions.assertEquals(expected, actual);
    }
}
