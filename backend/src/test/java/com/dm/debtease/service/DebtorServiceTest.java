package com.dm.debtease.service;

import com.dm.debtease.TestUtils;
import com.dm.debtease.model.Debtor;
import com.dm.debtease.model.dto.DebtorDTO;
import com.dm.debtease.repository.DebtorRepository;
import com.dm.debtease.service.impl.DebtorServiceImpl;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DebtorServiceTest {
    @Mock
    private DebtorRepository debtorRepository;
    @InjectMocks
    private DebtorServiceImpl debtorService;

    @Test
    void testGetAllCDebtors() {
        when(debtorRepository.findAll()).thenReturn(List.of(new Debtor()));
        List<Debtor> actualDebtors = debtorService.getAllDebtors();
        Assertions.assertNotNull(actualDebtors);
        Assertions.assertFalse(actualDebtors.isEmpty());
    }

    @Test
    void testGetDebtorById() {
        int id = 1;
        String name = "name";
        String surname = "surname";
        String email = "email@gmail.com";
        String phoneNumber = "+37067144213";
        Debtor expectedDebtor = TestUtils.setupDebtorTestData(name, surname, email, phoneNumber);
        when(debtorRepository.findById(id)).thenReturn(Optional.of(expectedDebtor));
        Debtor actualDebtor = debtorService.getDebtorById(id);
        Assertions.assertNotNull(actualDebtor);
        Assertions.assertEquals(expectedDebtor.getName(), actualDebtor.getName());
        Assertions.assertEquals(expectedDebtor.getSurname(), actualDebtor.getSurname());
    }

    @Test
    void testGetDebtorByNonExistingId() {
        int id = -1;
        when(debtorRepository.findById(id)).thenReturn(Optional.empty());
        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> debtorService.getDebtorById(id),
                "Expected getDebtorById to throw, but it didn't"
        );
        Assertions.assertTrue(thrown.getMessage().contains(String.format(Constants.DEBTOR_NOT_FOUND, id)));
    }

    @Test
    void testGetDebtorByUsername() {
        String username = "debtor123";
        String name = "name";
        String surname = "surname";
        String email = "email@gmail.com";
        String phoneNumber = "+37067144213";
        Debtor expectedDebtor = TestUtils.setupDebtorTestData(name, surname, email, phoneNumber);
        when(debtorRepository.findByUserUsername(username)).thenReturn(Optional.of(expectedDebtor));
        Debtor actualDebtor = debtorService.getDebtorByUsername(username);
        Assertions.assertNotNull(actualDebtor);
        Assertions.assertEquals(expectedDebtor.getName(), actualDebtor.getName());
        Assertions.assertEquals(expectedDebtor.getSurname(), actualDebtor.getSurname());
    }

    @Test
    void testGetDebtorByNonExistingUsername() {
        String username = "random";
        when(debtorRepository.findByUserUsername(username)).thenReturn(Optional.empty());
        Debtor actualDebtor = debtorService.getDebtorByUsername(username);
        Assertions.assertNull(actualDebtor);
    }

    @Test
    void testGetDebtorByNameAndSurname() {
        String name = "name";
        String surname = "surname";
        String email = "email@gmail.com";
        String phoneNumber = "+37067144213";
        Debtor expectedDebtor = TestUtils.setupDebtorTestData(name, surname, email, phoneNumber);
        when(debtorRepository.findByNameAndSurname(name, surname)).thenReturn(Optional.of(expectedDebtor));
        Debtor actualDebtor = debtorService.getDebtorByNameAndSurname(name, surname);
        Assertions.assertNotNull(actualDebtor);
        Assertions.assertEquals(expectedDebtor.getName(), actualDebtor.getName());
        Assertions.assertEquals(expectedDebtor.getSurname(), actualDebtor.getSurname());
    }

    @Test
    void testGetDebtorByNonExistingNameAndSurname() {
        String username = "random";
        when(debtorRepository.findByUserUsername(username)).thenReturn(Optional.empty());
        Debtor actualDebtor = debtorService.getDebtorByUsername(username);
        Assertions.assertNull(actualDebtor);
    }

    @Test
    void testEditDebtorById() {
        String name = "name";
        String surname = "surname";
        String email = "email@gmail.com";
        String phoneNumber = "+37067144213";
        String editedName = "editedName";
        String editedSurname = "editedSurname";
        String editedEmail = "editedEmail@gmail.com";
        String editedPhoneNumber = "+37067144213";
        int id = 1;
        Debtor debtor = TestUtils.setupDebtorTestData(name, surname, email, phoneNumber);
        Debtor editedDebtor =
                TestUtils.setupEditedDebtorTestData(editedName, editedSurname, editedEmail, editedPhoneNumber);
        DebtorDTO debtorDTO =
                TestUtils.setupDebtorDTOTestData(editedName, editedSurname, editedEmail, editedPhoneNumber);
        when(debtorRepository.findById(id)).thenReturn(Optional.of(debtor));
        when(debtorRepository.save(debtor)).thenReturn(editedDebtor);
        Debtor actualEditedDebtor = debtorService.editDebtorById(debtorDTO, id);
        Assertions.assertNotNull(actualEditedDebtor);
        Assertions.assertEquals(editedDebtor.getName(), actualEditedDebtor.getName());
        Assertions.assertEquals(editedDebtor.getSurname(), actualEditedDebtor.getSurname());
        Assertions.assertEquals(editedDebtor.getEmail(), actualEditedDebtor.getEmail());
        Assertions.assertEquals(editedDebtor.getPhoneNumber(), actualEditedDebtor.getPhoneNumber());
    }

    @Test
    void testEditDebtorByNonExistingId() {
        int id = -1;
        String editedName = "editedName";
        String editedSurname = "editedSurname";
        String editedEmail = "editedEmail@gmail.com";
        String editedPhoneNumber = "+37067144213";
        DebtorDTO debtorDTO =
                TestUtils.setupDebtorDTOTestData(editedName, editedSurname, editedEmail, editedPhoneNumber);
        when(debtorRepository.findById(id)).thenReturn(Optional.empty());
        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> debtorService.editDebtorById(debtorDTO, id),
                "Expected editDebtorById to throw, but it didn't"
        );
        Assertions.assertTrue(thrown.getMessage().contains(String.format(Constants.DEBTOR_NOT_FOUND, id)));
    }

    @Test
    void testCreateDebtor() {
        String editedName = "editedName";
        String editedSurname = "editedSurname";
        String editedEmail = "editedEmail@gmail.com";
        String editedPhoneNumber = "+37067144213";
        DebtorDTO debtorDTO =
                TestUtils.setupDebtorDTOTestData(editedName, editedSurname, editedEmail, editedPhoneNumber);
        Debtor createdDebtor =
                TestUtils.setupEditedDebtorTestData(editedName, editedSurname, editedEmail, editedPhoneNumber);
        when(debtorRepository.save(any(Debtor.class))).thenReturn(createdDebtor);
        Debtor actualCreatedDebtor = debtorService.createDebtor(debtorDTO);
        Assertions.assertNotNull(actualCreatedDebtor);
        Assertions.assertEquals(createdDebtor.getName(), actualCreatedDebtor.getName());
        Assertions.assertEquals(createdDebtor.getSurname(), actualCreatedDebtor.getSurname());
        Assertions.assertEquals(createdDebtor.getEmail(), actualCreatedDebtor.getEmail());
        Assertions.assertEquals(createdDebtor.getPhoneNumber(), actualCreatedDebtor.getPhoneNumber());
    }

    @Test
    void testDeleteDebtorById() {
        int id = 1;
        when(debtorRepository.findById(id)).thenReturn(Optional.of(new Debtor()));
        doNothing().when(debtorRepository).deleteById(id);
        Assertions.assertDoesNotThrow(() -> debtorService.deleteDebtorById(id));
    }

    @Test
    void testDeleteCreditorById() {
        int id = -1;
        when(debtorRepository.findById(id)).thenReturn(Optional.empty());
        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> debtorService.deleteDebtorById(id),
                "Expected deleteDebtorById to throw, but it didn't"
        );
        Assertions.assertTrue(thrown.getMessage().contains(String.format(Constants.DEBTOR_NOT_FOUND, id)));
    }
}