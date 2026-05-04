package be.crismartens.financetracker.repository;

import be.crismartens.financetracker.model.AppUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@SpringBootTest
@Sql({"/schema.sql", "/data.sql"})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("find user by username")
    void findUserByUsername() {
        // Act
        Optional<AppUser> user = userRepository.findByUsername("Mark");

        // Assert
        assertTrue(user.isPresent());
        assertEquals("Mark", user.get().getUsername());
        assertEquals("mark@google.com", user.get().getEmail());
        assertFalse(user.get().getPassword().isEmpty());
        assertEquals("ROLE_USER", user.get().getAuthority());
    }

    @Test
    @DisplayName("find user id by username")
    void findIdByUsername() {
        // Act
        Long id = userRepository.findIdByUsername("Mark").get();

        // Assert
        assertEquals(1, id);
    }
}