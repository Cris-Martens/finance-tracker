package be.crismartens.financetracker.unittesting.repository.repository;

import be.crismartens.financetracker.model.AppUser;
import be.crismartens.financetracker.model.Category;
import be.crismartens.financetracker.model.CategoryBudget;
import be.crismartens.financetracker.repository.BudgetRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql({"/schema.sql", "/data.sql"})
class BudgetRepositoryTest {

    @Autowired
    private BudgetRepository budgetRepository;

    // ------- Get budget by user Id tests -------

    @Test
    @DisplayName("Get Budgets By User Id - Success")
    void getBudgetByUserId() {
        // Act
        List<CategoryBudget> result = budgetRepository.getCategoryBudgetByAppUser_Id(1L);

        // Assert
        AppUser user =  new AppUser();
        user.setId(1L);
        user.setUsername("mark@google.com");
        user.setPassword("ValidPass123!");

        List<CategoryBudget> expected = new ArrayList<>();
        expected.add(new CategoryBudget(250.0, new Category(2L, "Utilities"), user));
        expected.add(new CategoryBudget(850.0, new Category(1L, "Housing"), user));
        expected.add(new CategoryBudget(50.0, new Category(5L, "Transportation"), user));

        assertThat(result.equals(expected));
    }

    @Test
    @DisplayName("Get Budgets By User Id - Empty")
    void getBudgetByUserIdEmpty() {
        // Act
        List<CategoryBudget> result = budgetRepository.getCategoryBudgetByAppUser_Id(2L);

        // Arrange
        List<CategoryBudget> expected = new ArrayList<>();

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Get budgets by User Id - nonexistent user")
    void getBudgetByUserIdNonexistentUser() {
        // Act
        List<CategoryBudget> result = budgetRepository.getCategoryBudgetByAppUser_Id(3L);

        // Assert
        List<CategoryBudget> expected = new ArrayList<>();

        assertEquals(expected, result);
    }

    // ------- Get budget by user Id tests -------

    @Test
    @DisplayName("Get budget by User Id & Category id - success")
    void getBudgetByUserIdAndCategoryIdSuccess() {
        // Act
        CategoryBudget result = budgetRepository.getCategoryBudgetByAppUser_IdAndCategory_Id(1L, 2L);

        // Assert
        assertNotNull(result);
        assertEquals(250.0, result.getAmount());
        assertEquals(new Category(2L, "Utilities"), result.getCategory());
    }

    @Test
    @DisplayName("Get budget by User Id & Category id - no budget")
    void getBudgetByUserIdAndCategoryIdNoBudget() {
        // Act
        CategoryBudget result = budgetRepository.getCategoryBudgetByAppUser_IdAndCategory_Id(1L, 3L);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Get budget by User Id & Category id - nonexistent user")
    void getBudgetByUserIdAndCategoryIdNonexistentUser() {
        // Act
        CategoryBudget result = budgetRepository.getCategoryBudgetByAppUser_IdAndCategory_Id(3L, 2L);

        // Assert
        assertNull(result);
    }
}