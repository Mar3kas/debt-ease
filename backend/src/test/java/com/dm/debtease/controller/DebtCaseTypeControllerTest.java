package com.dm.debtease.controller;

import com.dm.debtease.TestUtils;
import com.dm.debtease.model.DebtCaseType;
import com.dm.debtease.service.DebtCaseTypeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class DebtCaseTypeControllerTest {
    @Mock
    private DebtCaseTypeService debtCaseTypeService;
    @InjectMocks
    private DebtCaseTypeController debtCaseTypeController;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(debtCaseTypeController).build();
    }

    @Test
    void getAllDebtCaseTypes_ShouldReturnListOfDebtCaseTypes() throws Exception {
        List<DebtCaseType> mockDebtCaseTypes = List.of(TestUtils.setupDebtCaseTypeTestData("TAX_DEBT"));
        when(debtCaseTypeService.getAllDebtCaseTypes()).thenReturn(mockDebtCaseTypes);

        MvcResult result = mockMvc.perform(get("/api/debtcase/types"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type").value("TAX_DEBT"))
                .andDo(print())
                .andReturn();

        verify(debtCaseTypeService).getAllDebtCaseTypes();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType());
        Assertions.assertNotNull(result.getResponse().getContentAsString());
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }
}
