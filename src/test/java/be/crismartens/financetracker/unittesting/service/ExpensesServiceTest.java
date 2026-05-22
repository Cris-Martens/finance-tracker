package be.crismartens.financetracker.unittesting.service;

import be.crismartens.financetracker.CategoryNotFoundException;
import be.crismartens.financetracker.ExpenseNotFoundException;
import be.crismartens.financetracker.UnauthorisedAccessException;
import be.crismartens.financetracker.dto.ExpenseDTO;
import be.crismartens.financetracker.model.AppUser;
import be.crismartens.financetracker.model.Category;
import be.crismartens.financetracker.model.Expense;
import be.crismartens.financetracker.repository.CategoryRepository;
import be.crismartens.financetracker.repository.ExpensesRepository;
import be.crismartens.financetracker.repository.UserRepository;
import be.crismartens.financetracker.service.ExpensesService;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpensesServiceTest {
    @Mock
    private ExpensesRepository expensesRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;


    @InjectMocks
    private ExpensesService expensesService;

    private Expense expense1;
    private Expense expense2;
    private Expense expense3;

    private Category category1;
    private Category category2;

    private UserDetails principal1;
    private UserDetails principal2;
    private AppUser appUser;

    @BeforeEach
    void setUp() {
        // Set up test user
        appUser = new AppUser();
        appUser.setId(1L);
        appUser.setUsername("testUser");



        // Set up Principal
        principal1 = new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of();
            }

            @Override
            public @Nullable String getPassword() {
                return "";
            }

            @Override
            public String getUsername() {
                return "testUser";
            }
        };

        principal2 = new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of();
            }
            @Override
            public @Nullable String getPassword() {
                return "";
            }
            @Override
            public @Nullable String getUsername() {
                return "nonexistent";
            }
        };

        // Set up Categories
        category1 = new Category();
        category1.setId(1L);
        category1.setName("testCategory");

        category2 = new Category();
        category2.setId(3L);
        category2.setName("testCategory2");

        // Set up expenses
        expense1 = new Expense();
        expense1.setId(1L);
        expense1.setExpenseDate(LocalDate.parse("2026-04-23"));
        expense1.setCategory(category1);
        expense1.setAppUser(appUser);
        expense1.setAmount(40.0);
        expense1.setDescription("description1");

        expense2 = new Expense();
        expense2.setId(2L);
        expense2.setExpenseDate(LocalDate.parse("2026-03-23"));
        expense2.setCategory(category2);
        expense2.setAppUser(appUser);
        expense2.setAmount(25.99);
        expense2.setDescription("description2");

        expense3 = new Expense();
        expense3.setId(3L);
        expense3.setExpenseDate(LocalDate.parse("2026-04-12"));
        expense3.setCategory(category1);
        expense3.setAppUser(appUser);
        expense3.setAmount(79.99);
        expense3.setDescription("description3");
    }

    // =========== getExpensesByAppUserId() Tests ===========

    @Test
    void getExpensesByAppUser_WhenUserDoesNotExist_ThrowsExpenseNotFoundException() {
        // Arrange
        when(userRepository.findIdByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> expensesService.getExpensesByAppUserId(principal2));
        verify(userRepository, times(1)).findIdByUsername("nonexistent");
    }

    @Test
    void getExpensesByAppUser_WhenUserDoesExist_ReturnListExpenseDTO() {
        // Arrange
        List<ExpenseDTO> expenseDTOList = new ArrayList<>();
        expenseDTOList.add(new ExpenseDTO(expense1));
        expenseDTOList.add(new ExpenseDTO(expense2));
        expenseDTOList.add(new ExpenseDTO(expense3));

        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(expensesRepository.findExpensesByAppUserId(1L)).thenReturn(List.of(expense1,  expense2,  expense3));

        // Act
        List<ExpenseDTO> expenses = expensesService.getExpensesByAppUserId(principal1);

        // Assert
        assertEquals(expenseDTOList, expenses);
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(expensesRepository, times(1)).findExpensesByAppUserId(1L);
    }

    @Test
    void getExpensesByAppUser_WhenUserDoesExist_NoExpensesFound() {
        // Arrange
        List<ExpenseDTO> expenseDTOList = new ArrayList<>();

        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(expensesRepository.findExpensesByAppUserId(1L)).thenReturn(List.of());

        // Act
        List<ExpenseDTO> expenses = expensesService.getExpensesByAppUserId(principal1);

        // Assert
        assertEquals(expenseDTOList, expenses);
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(expensesRepository, times(1)).findExpensesByAppUserId(1L);
    }

    // =========== GetExpenseById() Tests ===========

    @Test

    void getExpenseById_WhenIdDoesNotExist_ThrowsExpenseNotFoundException() {
        // Arrange
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(expensesRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ExpenseNotFoundException.class, () -> expensesService.getExpenseById(1L, principal1));
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(expensesRepository, times(1)).findById(1L);
    }

    @Test
    void getExpenseById_WhenUserDoesNotExist_ThrowsUsernameNotFoundException() {
        // Arrange
        when(userRepository.findIdByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> expensesService.getExpenseById(1L, principal2));
        verify(userRepository, times(1)).findIdByUsername("nonexistent");
    }

    @Test
    void getExpenseById_WhenUserDoesNotOwnExpense_ThrowsUnauthorisedAccessException() {
        // Arrange
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(2L));
        when(expensesRepository.findById(1L)).thenReturn(Optional.of(expense1));

        // Act & Assert
        assertThrows(UnauthorisedAccessException.class, () -> expensesService.getExpenseById(1L, principal1));
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(expensesRepository, times(1)).findById(1L);
    }

    @Test
    void GetExpenseById_WhenUserOwnsExpense_ReturnsExpenseDTO() {
        // Arrange
        ExpenseDTO expectedExpenseDTO = new ExpenseDTO(expense1);

        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(expensesRepository.findById(1L)).thenReturn(Optional.of(expense1));

        // Act
        ExpenseDTO expense = expensesService.getExpenseById(1L, principal1);

        // Assert
        assertEquals(expectedExpenseDTO, expense);
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(expensesRepository, times(1)).findById(1L);
    }

    // =========== addExpense() Tests ===========

    @Test
    void addExpense_WhenUserDoesNotExist_ThrowsUsernameNotFoundException() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> expensesService.addExpense(expense1, principal2));
        verify(userRepository, times(1)).findByUsername("nonexistent");
        verify(expensesRepository, never()).save(any(Expense.class));
    }

    @Test
    void addExpense_CategoryDoesNotExist_ThrowsCategoryNotFoundException() {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(appUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Arrange
        assertThrows(CategoryNotFoundException.class, () -> expensesService.addExpense(expense1, principal1));
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(categoryRepository, times(1)).findById(1L);
        verify(expensesRepository, never()).save(any(Expense.class));
    }

    @Test
    void addExpense_WhenUserExists_SaveNewExpense() {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(appUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));

        // Act
        expensesService.addExpense(expense1, principal1);

        // Assert
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(categoryRepository, times(1)).findById(1L);
        verify(expensesRepository, times(1)).save(any(Expense.class));
    }

    // =========== updateExpense() Tests ===========

    @Test
    void updateExpense_WhenUserDoesNotExist_ThrowsUsernameNotFoundException() {
        // Arrange
        when(userRepository.findIdByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> expensesService.updateExpense(expense1, principal2));
        verify(userRepository, times(1)).findIdByUsername("nonexistent");
        verify(expensesRepository, never()).save(any(Expense.class));
    }

    @Test
    void UpdateExpense_ExpenseDoesNotExist_ThrowsExpenseNotFoundException() {
        // Arrange
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(expensesRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ExpenseNotFoundException.class, () -> expensesService.updateExpense(expense1, principal1));
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(expensesRepository, times(1)).findById(1L);
        verify(expensesRepository, never()).save(any(Expense.class));
    }

    @Test
    void updateExpense_UserDoesNotOwnExpense_ThrowsUnauthorisedAccessException() {
        // Arrange
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(2L));
        when(expensesRepository.findById(1L)).thenReturn(Optional.of(expense1));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));

        // Act & Assert
        assertThrows(UnauthorisedAccessException.class, () -> expensesService.updateExpense(expense1, principal1));
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(expensesRepository, times(1)).findById(1L);
        verify(expensesRepository, never()).save(any(Expense.class));
    }

    @Test
    void updateExpense_CategoryDoesNotExist_ThrowsCategoryNotFoundException() {
        // Arrange
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(expensesRepository.findById(1L)).thenReturn(Optional.of(expense1));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> expensesService.updateExpense(expense1, principal1));
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(expensesRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).findById(1L);
        verify(expensesRepository, never()).save(any(Expense.class));
    }

    @Test
    void updateExpense_UpdateExpenseDate() {
        // Arrange
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(expensesRepository.findById(1L)).thenReturn(Optional.of(expense1));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));

        expense1.setExpenseDate(LocalDate.parse("2026-05-03"));

        // Act
        expensesService.updateExpense(expense1, principal1);

        // Assert
        assertEquals(LocalDate.parse("2026-05-03"), expense1.getExpenseDate());
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(expensesRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).findById(1L);
        verify(expensesRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void updateExpense_UpdateCategory() {
        // Arrange
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(expensesRepository.findById(1L)).thenReturn(Optional.of(expense1));
        when(categoryRepository.findById(3L)).thenReturn(Optional.of(category2));

        expense1.setCategory(category2);

        // Act
        expensesService.updateExpense(expense1, principal1);

        // Assert
        assertEquals(category2, expense1.getCategory());
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(expensesRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).findById(3L);
        verify(expensesRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void updateExpense_UpdateAmount() {
        // Arrange
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(expensesRepository.findById(1L)).thenReturn(Optional.of(expense1));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));

        expense1.setAmount(50.0);

        // Act
        expensesService.updateExpense(expense1, principal1);

        // Assert
        assertEquals(50.0, expense1.getAmount());
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(expensesRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).findById(1L);
        verify(expensesRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void updateExpense_UpdateDescription() {
        // Arrange
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(expensesRepository.findById(1L)).thenReturn(Optional.of(expense1));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));

        expense1.setDescription("description4");

        // Act
        expensesService.updateExpense(expense1, principal1);

        // Assert
        assertEquals("description4", expense1.getDescription());
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(expensesRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).findById(1L);
        verify(expensesRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void updateExpense_WhenExists_IgnoresNullValues() {
        // Arrange
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(expensesRepository.findById(1L)).thenReturn(Optional.of(expense1));

        LocalDate originalDate = expense1.getExpenseDate();
        Category originalCategory = expense1.getCategory();
        Double originalAmount = expense1.getAmount();
        String originalDescription = expense1.getDescription();

        Expense updateExpense = new Expense();
        updateExpense.setId(1L);
        // All null except id - should not change anything

        // Act
        expensesService.updateExpense(updateExpense, principal1);

        // Assert
        assertEquals(originalDate, expense1.getExpenseDate());
        assertEquals(originalCategory, expense1.getCategory());
        assertEquals(originalAmount, expense1.getAmount());
        assertEquals(originalDescription, expense1.getDescription());
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(expensesRepository, times(1)).findById(1L);
        verify(expensesRepository, times(1)).save(any(Expense.class));
    }

    // =========== deleteExpense() Tests ===========

    @Test
    void deleteExpense_WhenUserDoesNotExist_ThrowsUsernameNotFoundException() {
        // Arrange
        when(userRepository.findIdByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> expensesService.deleteExpense(expense1, principal2));
        verify(userRepository, times(1)).findIdByUsername("nonexistent");
        verify(expensesRepository, never()).delete(any(Expense.class));
    }

    @Test
    void deleteExpense_WhenExpenseDoesNotExist_ThrowsExpenseNotFoundException() {
        // Arrange
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(expensesRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ExpenseNotFoundException.class, () -> expensesService.deleteExpense(expense1, principal1));
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(expensesRepository, times(1)).findById(1L);
        verify(expensesRepository, never()).delete(any(Expense.class));

        // Assert
    }

    @Test
    void deleteExpense_UserDoesNotOwnExpense_UnauthorisedAccessException() {
        // Arrange
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(2L));
        when(expensesRepository.findById(1L)).thenReturn(Optional.of(expense1));

        // Act & Assert
        assertThrows(UnauthorisedAccessException.class, () -> expensesService.deleteExpense(expense1, principal1));
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(expensesRepository, times(1)).findById(1L);
        verify(expensesRepository, never()).delete(any(Expense.class));
    }

    @Test
    void deleteExpense_deleteExpense() {
        // Arrange
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(expensesRepository.findById(1L)).thenReturn(Optional.of(expense1));

        // Act
        expensesService.deleteExpense(expense1, principal1);

        // Assert
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(expensesRepository, times(1)).findById(1L);
        verify(expensesRepository, times(1)).delete(any(Expense.class));
    }
}