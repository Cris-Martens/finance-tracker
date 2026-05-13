package be.crismartens.financetracker.repository;

import be.crismartens.financetracker.model.AppUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@Sql({"/schema.sql", "/data.sql"})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("find user by username")
    void findUserByUsername() {
        // Act
        Optional<AppUser> user = userRepository.findByUsername("mark@google.com");

        // Assert
        assertTrue(user.isPresent());
        assertEquals("mark@google.com", user.get().getUsername());
        assertFalse(user.get().getPassword().isEmpty());
        assertEquals("ROLE_USER", user.get().getAuthority());
    }

    @Test
    @DisplayName("find user id by username")
    void findIdByUsername() {
        // Act
        Long id = userRepository.findIdByUsername("mark@google.com").get();

        // Assert
        assertEquals(1, id);
    }
}