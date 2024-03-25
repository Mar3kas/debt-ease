package com.dm.debtease.controller;

import com.dm.debtease.TestUtils;
import com.dm.debtease.model.Debtor;
import com.dm.debtease.model.dto.DebtorDTO;
import com.dm.debtease.service.DebtorService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class DebtorControllerTest {
    @Mock
    private DebtorService debtorService;
    @InjectMocks
    private DebtorController debtorController;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(debtorController).build();
    }

    @Test
    void testGetAllDebtors() throws Exception {
        String username = "debtor";
        String name = "name";
        String surname = "surname";
        String email = "email@gmail.com";
        String phoneNumber = "+37067144213";
        List<Debtor> mockedDebtor = List.of(TestUtils.setupDebtorTestData(name, surname, email, phoneNumber, username));
        when(debtorService.getAllDebtors()).thenReturn(mockedDebtor);
        MvcResult result = mockMvc.perform(get("/api/debtors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(name))
                .andDo(print())
                .andReturn();
        verify(debtorService).getAllDebtors();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType());
        Assertions.assertNotNull(result.getResponse().getContentAsString());
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    void testGetDebtorById() throws Exception {
        int id = 1;
        String username = "debtor";
        String name = "name";
        String surname = "surname";
        String email = "email@gmail.com";
        String phoneNumber = "+37067144213";
        Debtor mockedDebtor = TestUtils.setupDebtorTestData(name, surname, email, phoneNumber, username);
        when(debtorService.getDebtorById(id)).thenReturn(mockedDebtor);
        MvcResult result = mockMvc.perform(get("/api/debtors/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(name))
                .andDo(print())
                .andReturn();
        verify(debtorService).getDebtorById(anyInt());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType());
        Assertions.assertNotNull(result.getResponse().getContentAsString());
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    void testEditDebtorById() throws Exception {
        String editedName = "editedName";
        String editedSurname = "editedSurname";
        String editedEmail = "editedEmail@gmail.com";
        String editedPhoneNumber = "+37067144213";
        int id = 1;
        Debtor expectedEditedDebtor =
                TestUtils.setupEditedDebtorTestData(editedName, editedSurname, editedEmail, editedPhoneNumber);
        DebtorDTO mockedDebtorDTO =
                TestUtils.setupDebtorDTOTestData(editedName, editedSurname, editedEmail, editedPhoneNumber);
        when(debtorService.editDebtorById(any(DebtorDTO.class), anyInt())).thenReturn(expectedEditedDebtor);
        MvcResult result = mockMvc.perform(put("/api/debtors/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mockedDebtorDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(expectedEditedDebtor.getEmail()))
                .andExpect(jsonPath("$.name").value(expectedEditedDebtor.getName()))
                .andDo(print())
                .andReturn();
        verify(debtorService).editDebtorById(any(DebtorDTO.class), anyInt());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType());
        Assertions.assertNotNull(result.getResponse().getContentAsString());
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    void testDeleteDebtorById() throws Exception {
        int id = 1;
        when(debtorService.deleteDebtorById(id)).thenReturn(true);
        MvcResult result = mockMvc.perform(delete("/api/debtors/{id}", id))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andReturn();
        verify(debtorService).deleteDebtorById(anyInt());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus());
    }

    @Test
    void testDeleteDebtorByIdError() throws Exception {
        int id = 1;
        when(debtorService.deleteDebtorById(id)).thenReturn(false);
        MvcResult result = mockMvc.perform(delete("/api/debtors/{id}", id))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Error deleting debtor!"))
                .andDo(print())
                .andReturn();
        verify(debtorService).deleteDebtorById(anyInt());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType());
        Assertions.assertNotNull(result.getResponse().getContentAsString());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }
}
