package be.crismartens.financetracker.unittesting.service;

import be.crismartens.financetracker.exceptions.AccountInfoNotFoundException;
import be.crismartens.financetracker.dto.AccountInfoDTO;
import be.crismartens.financetracker.model.AccountInfo;
import be.crismartens.financetracker.model.AppUser;
import be.crismartens.financetracker.repository.AccountRepository;
import be.crismartens.financetracker.repository.UserRepository;
import be.crismartens.financetracker.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AccountService accountService;

    private AppUser testUser;
    private AccountInfo testAccountInfo;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new AppUser();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        // Setup test account info
        testAccountInfo = new AccountInfo();
        testAccountInfo.setId(1L);
        testAccountInfo.setFirstName("John");
        testAccountInfo.setLastName("Doe");
        testAccountInfo.setCountry("Belgium");
        testAccountInfo.setMonthlyIncome(2400.00);
        testAccountInfo.setAppUser(testUser);
    }

    // ============== findAppUserId() Tests ==============

    @Test
    void findAppUserId_WhenUserExists_ReturnUserId() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));

        // Act
        Long userId = accountService.findAppUserId(userDetails);

        // Assert
        assertEquals(1L, userId);
        verify(userRepository, times(1)).findIdByUsername("testUser");
    }

    @Test
    void findAppUserId_WhenUserNotFound_ThrowsUsernameNotFoundException() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("nonexistent");
        when(userRepository.findIdByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> accountService.findAppUserId(userDetails));
        verify(userRepository, times(1)).findIdByUsername("nonexistent");
    }

    // ============== upsertAccountInfo() Tests ==============

    @Test
    void upsertAccountInfo_WhenAccountInfoExists_UpdatesFirstName() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(accountRepository.findByAppUser_Id(1L)).thenReturn(Optional.of(testAccountInfo));

        AccountInfo updateInfo = new AccountInfo();
        updateInfo.setFirstName("Jane");

        // Act
        accountService.updateAccountInfo(updateInfo, userDetails);

        // Assert
        assertEquals("Jane", testAccountInfo.getFirstName());
        verify(accountRepository, times(1)).save(testAccountInfo);
    }

    @Test
    void upsertAccountInfo_WhenAccountInfoExists_UpdatesLastName() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(accountRepository.findByAppUser_Id(1L)).thenReturn(Optional.of(testAccountInfo));

        AccountInfo updateInfo = new AccountInfo();
        updateInfo.setLastName("Smith");

        // Act
        accountService.updateAccountInfo(updateInfo, userDetails);

        // Assert
        assertEquals("Smith", testAccountInfo.getLastName());
        verify(accountRepository, times(1)).save(testAccountInfo);
    }

    // UpdatesCountry
    // UpdatesMonthlyIncome

    @Test
    void upsertAccountInfo_WhenAccountInfoExists_IgnoresNullValues(){
        // Arrange
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(accountRepository.findByAppUser_Id(1L)).thenReturn(Optional.of(testAccountInfo));

        String originalFristName = testAccountInfo.getFirstName();
        String originalLastName = testAccountInfo.getLastName();

        AccountInfo updateInfo = new AccountInfo();
        // All null - should not change anything

        // Act
        accountService.updateAccountInfo(updateInfo, userDetails);

        // Assert
        assertEquals(originalFristName, testAccountInfo.getFirstName());
        assertEquals(originalLastName, testAccountInfo.getLastName());
        verify(accountRepository, times(1)).save(testAccountInfo);
    }

    @Test
    void upsertAccountInfo_WhenUserNotFound_ThrowsException() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("nonexistent");
        when(userRepository.findIdByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class,
                () -> accountService.updateAccountInfo(testAccountInfo, userDetails));
    }

    // ============== getAccountInfo() Tests ==============

    @Test
    void getAccountInfo_WhenAccountInfoExists_ReturnsAccountInfoDTO() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(accountRepository.findByAppUser_Id(1L)).thenReturn(Optional.of(testAccountInfo));

        // Act
        AccountInfoDTO result = accountService.getAccountInfo(userDetails);

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("Belgium", result.getCountry());
        assertEquals(2400.0, result.getMonthlyIncome());
        verify(accountRepository, times(1)).findByAppUser_Id(1L);
    }

    @Test
    void getAccountInfo_WhenAccountInfoDoesNotExist_ReturnsEmptyAccountInfoDTO() {
        // Assert
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(accountRepository.findByAppUser_Id(1L)).thenReturn(Optional.of(testAccountInfo));

        // Act
        AccountInfoDTO result = accountService.getAccountInfo(userDetails);

        // Assert
        assertNotNull(result);
        verify(accountRepository, times(1)).findByAppUser_Id(1L);
    }

    @Test
    void getAccountInfo_WhenUserNotFound_ThrowsException() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("nonexistent");
        when(userRepository.findIdByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> accountService.getAccountInfo(userDetails));
    }

    // ============== deleteAccountInfo() Tests ==============

    @Test
    void deleteAccountInfo_WhenAccountInfoExists_DeletesSuccessfully() {
        // Assert
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(accountRepository.findByAppUser_Id(1L)).thenReturn(Optional.of(testAccountInfo));

        // Act
        accountService.deleteAccountInfo(userDetails);

        // Assert
        verify(accountRepository, times(1)).delete(testAccountInfo);
    }

    @Test
    void deleteAccountInfo_WhenAccountInfoDoesNotExist_DoesNothing() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(accountRepository.findByAppUser_Id(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountInfoNotFoundException.class, () -> accountService.deleteAccountInfo(userDetails));

        verify(accountRepository, never()).delete(testAccountInfo);
    }

    @Test
    void deleteAccountInfo_WhenUserNotFound_ThrowsException() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("nonexistent");
        when(userRepository.findIdByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class,
                () -> accountService.deleteAccountInfo(userDetails));
    }
}