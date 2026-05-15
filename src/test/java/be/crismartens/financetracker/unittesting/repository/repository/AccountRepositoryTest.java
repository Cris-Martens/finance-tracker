package be.crismartens.financetracker.unittesting.repository.repository;

import be.crismartens.financetracker.model.AccountInfo;
import be.crismartens.financetracker.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql({"/schema.sql", "/data.sql"})
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    // -------- Find By User Id Tests --------

    @Test
    @DisplayName("Find By App User Id - Success")
    void findByAppUserIdSuccess() {
        // Act
        Optional<AccountInfo> result = accountRepository.findByAppUser_Id(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getAppUser().getId());
        assertEquals("Mark", result.get().getFirstName());
        assertEquals("Marquez", result.get().getLastName());
        assertEquals("Belgium", result.get().getCountry());
        assertEquals(2400.0, result.get().getMonthlyIncome());
    }

    @Test
    @DisplayName("Find By App User Id - Nonexistent")
    void findByAppUserIdNonexistent() {
        // Act
        Optional<AccountInfo> result = accountRepository.findByAppUser_Id(3L);

        // Assert
        assertFalse(result.isPresent());
    }

    // -------- Find Monthly income By User Id Tests --------

    @Test
    @DisplayName("Find Monthly income by user - Success")
    void findMonthlyIncomeByUserSuccess() {
        // Act
        Double result = accountRepository.findMonthlyIncomeByAppUser_Id(1L);

        // Assert
        assertEquals(2400.0, result);
    }

    @Test
    @DisplayName("Find Monthly income by user - no income")
    void findMonthlyIncomeByUserNoIncome() {
        // Act
        Double result = accountRepository.findMonthlyIncomeByAppUser_Id(2L);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Find Monthly income by user - nonexistent user")
    void findMonthlyIncomeByUserNonexistentUser() {
        // Act
        Double result = accountRepository.findMonthlyIncomeByAppUser_Id(3L);

        // Assert
        assertNull(result);
    }
}