package com.dm.debtease.service;

import com.dm.debtease.TestUtils;
import com.dm.debtease.model.Creditor;
import com.dm.debtease.model.CustomUser;
import com.dm.debtease.model.Role;
import com.dm.debtease.model.dto.CreditorDTO;
import com.dm.debtease.repository.CreditorRepository;
import com.dm.debtease.repository.CustomUserRepository;
import com.dm.debtease.repository.RoleRepository;
import com.dm.debtease.service.impl.CreditorServiceImpl;
import com.dm.debtease.utils.Constants;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unused")
public class CreditorServiceTest {
    @Mock
    private CreditorRepository creditorRepository;
    @Mock
    private CustomUserRepository customUserRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private PasswordGeneratorService passwordGeneratorService;
    @Mock
    private DebtCaseService debtCaseService;
    @InjectMocks
    private CreditorServiceImpl creditorService;

    @Test
    void getAllCreditors_WhenCreditorsExist_ShouldReturnListOfCreditors() {
        when(creditorRepository.findAll()).thenReturn(List.of(new Creditor()));

        List<Creditor> actualCreditors = creditorService.getAllCreditors();

        Assertions.assertNotNull(actualCreditors);
        Assertions.assertFalse(actualCreditors.isEmpty());
    }

    @Test
    void getCreditorById_WhenCreditorExists_ShouldReturnCreditor() {
        String username = "creditor123";
        int id = 1;
        Creditor expectedCreditor = TestUtils.setupCreditorTestData(username, id);
        when(creditorRepository.findById(id)).thenReturn(Optional.of(expectedCreditor));

        Creditor actualCreditor = creditorService.getCreditorById(id);

        Assertions.assertNotNull(actualCreditor);
        Assertions.assertEquals(expectedCreditor.getUser().getUsername(), actualCreditor.getUser().getUsername());
    }

    @Test
    void getCreditorById_WhenCreditorDoesNotExist_ShouldThrowException() {
        int id = -1;
        when(creditorRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> creditorService.getCreditorById(id),
                "Expected getCreditorById to throw, but it didn't"
        );

        Assertions.assertTrue(thrown.getMessage().contains(String.format(Constants.CREDITOR_NOT_FOUND, id)));
    }

    @Test
    void getCreditorByUsername_WhenCreditorExists_ShouldReturnCreditor() {
        String username = "creditor123";
        int id = 1;
        Creditor expectedCreditor = TestUtils.setupCreditorTestData(username, id);
        when(creditorRepository.findByUserUsername(username)).thenReturn(Optional.of(expectedCreditor));

        Creditor actualCreditor = creditorService.getCreditorByUsername(username);

        Assertions.assertNotNull(actualCreditor);
        Assertions.assertEquals(expectedCreditor.getUser().getUsername(), actualCreditor.getUser().getUsername());
        Assertions.assertEquals(expectedCreditor.getId(), actualCreditor.getId());
    }

    @Test
    void getCreditorByUsername_WhenCreditorDoesNotExist_ShouldReturnNull() {
        String username = "random";
        when(creditorRepository.findByUserUsername(username)).thenReturn(Optional.empty());

        Creditor actualCreditor = creditorService.getCreditorByUsername(username);

        Assertions.assertNull(actualCreditor);
    }

    @Test
    void editCreditorById_WhenCreditorExists_ShouldEditAndReturnCreditor() {
        String name = "name";
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
        CreditorDTO creditorDTO =
                TestUtils.setupCreditorDTOTestData(editedName, editedEmail, editedAddress, editedPhoneNumber,
                        editedAccountNumber);
        when(creditorRepository.findById(id)).thenReturn(Optional.of(new Creditor()));
        when(creditorRepository.save(any(Creditor.class))).thenReturn(expectedEditedCreditor);

        Creditor actualEditedCreditor = creditorService.editCreditorById(creditorDTO, id);

        Assertions.assertNotNull(actualEditedCreditor);
        Assertions.assertEquals(expectedEditedCreditor.getName(), actualEditedCreditor.getName());
        Assertions.assertEquals(expectedEditedCreditor.getEmail(), actualEditedCreditor.getEmail());
        Assertions.assertEquals(expectedEditedCreditor.getAddress(), actualEditedCreditor.getAddress());
        Assertions.assertEquals(expectedEditedCreditor.getPhoneNumber(), actualEditedCreditor.getPhoneNumber());
        Assertions.assertEquals(expectedEditedCreditor.getAccountNumber(), actualEditedCreditor.getAccountNumber());
    }

    @Test
    void editCreditorById_WhenCreditorDoesNotExist_ShouldThrowException() {
        int id = -1;
        String editedName = "editedName";
        String editedEmail = "editedEmail@gmail.com";
        String editedAddress = "editedAddress";
        String editedPhoneNumber = "+37067144213";
        String editedAccountNumber = "editedAccountNumber";
        CreditorDTO creditorDTO =
                TestUtils.setupCreditorDTOTestData(editedName, editedEmail, editedAddress, editedPhoneNumber,
                        editedAccountNumber);
        when(creditorRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> creditorService.editCreditorById(creditorDTO, id),
                "Expected getCreditorById to throw, but it didn't"
        );

        Assertions.assertTrue(thrown.getMessage().contains(String.format(Constants.CREDITOR_NOT_FOUND, id)));
    }

    @Test
    void createCreditor_WhenRoleIsFound_ShouldCreateAndReturnCreditor() {
        String name = "name";
        String email = "email@gmail.com";
        String address = "address";
        String phoneNumber = "+37067144213";
        String accountNumber = "accountNumber";
        String roleName = "role";
        String username = "username";
        int roleId = 3;
        CreditorDTO creditorDTO = TestUtils.setupCreditorDTOTestData(name, email, address, phoneNumber, accountNumber);
        Creditor createdCreditor =
                TestUtils.setupCreditorTestData(name, email, address, phoneNumber, accountNumber, username);
        Role role = TestUtils.setupRoleTestData(roleName, roleId);
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(customUserRepository.save(any(CustomUser.class))).thenReturn(new CustomUser());
        when(passwordGeneratorService.generatePassword(anyInt())).thenReturn("generatedPassword");
        when(bCryptPasswordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(creditorRepository.save(any(Creditor.class))).thenReturn(createdCreditor);

        Creditor actualCreatedCreditor = creditorService.createCreditor(creditorDTO);

        Assertions.assertNotNull(actualCreatedCreditor);
        Assertions.assertEquals(createdCreditor.getName(), actualCreatedCreditor.getName());
        Assertions.assertEquals(createdCreditor.getEmail(), actualCreatedCreditor.getEmail());
        Assertions.assertEquals(createdCreditor.getAddress(), actualCreatedCreditor.getAddress());
        Assertions.assertEquals(createdCreditor.getPhoneNumber(), actualCreatedCreditor.getPhoneNumber());
        Assertions.assertEquals(createdCreditor.getAccountNumber(), actualCreatedCreditor.getAccountNumber());
    }

    @Test
    void createCreditor_WhenRoleIsNotFound_ShouldThrowException() {
        String name = "name";
        String email = "email@gmail.com";
        String address = "address";
        String phoneNumber = "+37067144213";
        String accountNumber = "accountNumber";
        CreditorDTO creditorDTO = TestUtils.setupCreditorDTOTestData(name, email, address, phoneNumber, accountNumber);
        when(roleRepository.findById(3)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> creditorService.createCreditor(creditorDTO),
                "Expected createCreditor to throw EntityNotFoundException when role not found"
        );

        Assertions.assertTrue(thrown.getMessage().contains(String.format(Constants.ROLE_NOT_FOUND, "3")));
    }

    @Test
    void deleteCreditorById_WhenCreditorExists_ShouldNotThrowException() {
        int id = 1;
        when(creditorRepository.findById(id)).thenReturn(Optional.of(new Creditor()));
        doNothing().when(creditorRepository).deleteById(id);

        Assertions.assertDoesNotThrow(() -> creditorService.deleteCreditorById(id));
    }

    @Test
    void deleteCreditorById_WhenCreditorDoesNotExist_ShouldThrowException() {
        int id = 100;
        when(creditorRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> creditorService.deleteCreditorById(id),
                "Expected deleteCreditorById to throw, but it didn't"
        );

        Assertions.assertTrue(thrown.getMessage().contains(String.format(Constants.CREDITOR_NOT_FOUND, id)));
    }
}
