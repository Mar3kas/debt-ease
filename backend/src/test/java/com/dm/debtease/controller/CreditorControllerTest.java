package com.dm.debtease.controller;

import com.dm.debtease.TestUtils;
import com.dm.debtease.model.Creditor;
import com.dm.debtease.model.dto.CreditorDTO;
import com.dm.debtease.service.CreditorService;
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
public class CreditorControllerTest {
    @Mock
    private CreditorService creditorService;
    @InjectMocks
    private CreditorController creditorController;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(creditorController).build();
    }

    @Test
    void getAllCreditors_ShouldReturnListOfCreditors() throws Exception {
        String name = "name";
        String email = "email@gmail.com";
        String address = "address";
        String phoneNumber = "+37067144213";
        String accountNumber = "accountNumber";
        String username = "username";
        List<Creditor> mockedCreditors = List.of(TestUtils.setupCreditorTestData(name, email, address, phoneNumber,
                accountNumber, username));
        when(creditorService.getAllCreditors()).thenReturn(mockedCreditors);

        MvcResult result = mockMvc.perform(get("/api/creditors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(name))
                .andExpect(jsonPath("$[0].email").value(email))
                .andExpect(jsonPath("$[0].address").value(address))
                .andExpect(jsonPath("$[0].phoneNumber").value(phoneNumber))
                .andExpect(jsonPath("$[0].accountNumber").value(accountNumber))
                .andDo(print())
                .andReturn();

        verify(creditorService).getAllCreditors();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType());
        Assertions.assertNotNull(result.getResponse().getContentAsString());
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    void getCreditorById_ShouldReturnCreditor() throws Exception {
        int id = 1;
        String name = "name";
        String email = "email@gmail.com";
        String address = "address";
        String phoneNumber = "+37067144213";
        String accountNumber = "accountNumber";
        String username = "username";
        Creditor mockedCreditor = TestUtils.setupCreditorTestData(name, email, address, phoneNumber,
                accountNumber, username);
        when(creditorService.getCreditorById(id)).thenReturn(mockedCreditor);

        MvcResult result = mockMvc.perform(get("/api/creditors/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.address").value(address))
                .andExpect(jsonPath("$.phoneNumber").value(phoneNumber))
                .andExpect(jsonPath("$.accountNumber").value(accountNumber))
                .andDo(print())
                .andReturn();

        verify(creditorService).getCreditorById(anyInt());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType());
        Assertions.assertNotNull(result.getResponse().getContentAsString());
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    void editCreditorById_ShouldReturnEditedCreditor() throws Exception {
        String editedName = "editedName";
        String editedEmail = "editedEmail@gmail.com";
        String editedAddress = "editedAddress";
        String editedPhoneNumber = "+37067144213";
        String editedAccountNumber = "editedAccountNumber";
        String username = "username";
        int id = 1;
        Creditor expectedEditedCreditor =
                TestUtils.setupCreditorTestData(editedName, editedEmail, editedAddress, editedPhoneNumber,
                        editedAccountNumber, username);
        CreditorDTO mockedCreditorDTO =
                TestUtils.setupCreditorDTOTestData(editedName, editedEmail, editedAddress, editedPhoneNumber,
                        editedAccountNumber);
        when(creditorService.editCreditorById(any(CreditorDTO.class), anyInt())).thenReturn(expectedEditedCreditor);

        MvcResult result = mockMvc.perform(put("/api/creditors/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mockedCreditorDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(expectedEditedCreditor.getEmail()))
                .andExpect(jsonPath("$.name").value(expectedEditedCreditor.getName()))
                .andExpect(jsonPath("$.address").value(expectedEditedCreditor.getAddress()))
                .andExpect(jsonPath("$.phoneNumber").value(expectedEditedCreditor.getPhoneNumber()))
                .andExpect(jsonPath("$.accountNumber").value(expectedEditedCreditor.getAccountNumber()))
                .andDo(print())
                .andReturn();

        verify(creditorService).editCreditorById(any(CreditorDTO.class), anyInt());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType());
        Assertions.assertNotNull(result.getResponse().getContentAsString());
        Assertions.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    void createCreditor_ShouldReturnCreatedCreditor() throws Exception {
        String name = "name";
        String email = "email@gmail.com";
        String address = "address";
        String phoneNumber = "+37067144213";
        String accountNumber = "accountNumber";
        String username = "username";
        CreditorDTO mockedCreditorDTO =
                TestUtils.setupCreditorDTOTestData(name, email, address, phoneNumber, accountNumber);
        Creditor expectedCreatedCreditor =
                TestUtils.setupCreditorTestData(name, email, address, phoneNumber, accountNumber, username);
        when(creditorService.createCreditor(any(CreditorDTO.class))).thenReturn(expectedCreatedCreditor);

        MvcResult result = mockMvc.perform(post("/api/creditors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mockedCreditorDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(expectedCreatedCreditor.getEmail()))
                .andExpect(jsonPath("$.name").value(expectedCreatedCreditor.getName()))
                .andExpect(jsonPath("$.address").value(expectedCreatedCreditor.getAddress()))
                .andExpect(jsonPath("$.phoneNumber").value(expectedCreatedCreditor.getPhoneNumber()))
                .andExpect(jsonPath("$.accountNumber").value(expectedCreatedCreditor.getAccountNumber()))
                .andDo(print())
                .andReturn();

        verify(creditorService).createCreditor(any(CreditorDTO.class));
        Assertions.assertNotNull(result);
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType());
        Assertions.assertNotNull(result.getResponse().getContentAsString());
        Assertions.assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
    }

    @Test
    void deleteCreditorById_ShouldReturnNoContent() throws Exception {
        int id = 1;
        when(creditorService.deleteCreditorById(id)).thenReturn(true);

        MvcResult result = mockMvc.perform(delete("/api/creditors/{id}", id))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andReturn();

        verify(creditorService).deleteCreditorById(anyInt());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus());
    }

    @Test
    void deleteCreditorById_WithActiveDebtCases_ShouldReturnBadRequest() throws Exception {
        int id = 1;
        when(creditorService.deleteCreditorById(id)).thenReturn(false);

        MvcResult result = mockMvc.perform(delete("/api/creditors/{id}", id))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Creditor has active debt cases!"))
                .andDo(print())
                .andReturn();

        verify(creditorService).deleteCreditorById(anyInt());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType());
        Assertions.assertNotNull(result.getResponse().getContentAsString());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }
}
