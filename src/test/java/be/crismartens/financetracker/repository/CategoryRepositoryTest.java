package be.crismartens.financetracker.repository;

import be.crismartens.financetracker.model.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql({"/schema.sql", "/data.sql"})
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    // ------- Find Category By Name Tests -------

    @Test
    @DisplayName("Find Category By Name - Success")
    void findCategoryByName() {
        // Act
        Optional<Category> result = categoryRepository.findByName("Housing");

        // Assert
        assertTrue(result.isPresent());
        assertThat(new Category(1L, "Housing").equals(result));
    }

    @Test
    @DisplayName("Find Category By Name - Nonexistent")
    void findCategoryByNameNonexistent() {
        // Act
        Optional<Category> result = categoryRepository.findByName("Nonexistent");

        // Assert
        assertFalse(result.isPresent());
    }

    // ------- Find Id By Name Tests -------

    @Test
    @DisplayName("Find Id By Name - Success")
    void findIdByName() {
        // Act
        Long result = categoryRepository.findIdByName("Housing");

        // Assert
        assertEquals(1L, result);
    }

    @Test
    @DisplayName("Find Id By Name - Nonexistent")
    void findIdByNameNonexistent() {
        // Act
        Long result = categoryRepository.findIdByName("Nonexistent");

        // Assert
        assertNull(result);
    }
}