package be.crismartens.financetracker.unittesting.service;

import be.crismartens.financetracker.CategoryBudgetNotFoundException;
import be.crismartens.financetracker.CategoryNotFoundException;
import be.crismartens.financetracker.model.AppUser;
import be.crismartens.financetracker.model.Category;
import be.crismartens.financetracker.model.CategoryBudget;
import be.crismartens.financetracker.repository.BudgetRepository;
import be.crismartens.financetracker.repository.CategoryRepository;
import be.crismartens.financetracker.repository.UserRepository;
import be.crismartens.financetracker.service.BudgetService;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BudgetService budgetService;

    AppUser appUser;

    UserDetails principal;

    Map<String, Double> budgets;

    CategoryBudget budget1;
    CategoryBudget budget2;

    Category category1;
    Category category2;

    @BeforeEach
    void setUp() {
        appUser = new AppUser();
        appUser.setId(1L);
        appUser.setUsername("testUser");

        principal = new UserDetails() {
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

        category1 = new Category();
        category1.setName("Category 1");
        category1.setId(1L);

        category2 = new Category();
        category2.setName("Category 2");
        category2.setId(2L);

        budget1 = new CategoryBudget();
        budget1.setId(1L);
        budget1.setCategory(category1);
        budget1.setAmount(100.0);
        budget1.setAppUser(appUser);

        budget2 = new CategoryBudget();
        budget2.setId(2L);
        budget2.setCategory(category2);
        budget2.setAmount(200.0);
        budget2.setAppUser(appUser);

        budgets = new HashMap<>();
        budgets.put(category1.getName(), 100.0);
        budgets.put(category2.getName(), 200.0);
    }

    // ============= getAllBudgets() Tests =============

    @Test
    @DisplayName("GET all budgets - success")
    void getAllBudgets() {
        // Arrange
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(budgetRepository.getCategoryBudgetByAppUser_Id(1L)).thenReturn(List.of(budget1, budget2));

        // Act
        Map<String, Double> result = budgetService.getAllBudgets(principal);

        // Assert
        assertEquals(budgets.get("Category 1"), result.get("Category 1"));
        assertEquals(budgets.get("Category 2"), result.get("Category 2"));

        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(budgetRepository, times(1)).getCategoryBudgetByAppUser_Id(1L);
    }

    @Test
    @DisplayName("Get all budgets - empty list")
    void getAllBudgetsEmptyList() {
        // Arrange
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(budgetRepository.getCategoryBudgetByAppUser_Id(1L)).thenReturn(Collections.emptyList());

        // Act
        Map<String, Double> result = budgetService.getAllBudgets(principal);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(budgetRepository, times(1)).getCategoryBudgetByAppUser_Id(1L);
    }

    @Test
    @DisplayName("GET all budgets - user does NOT exist")
    void getAllBudgetsUserDoesNotExist() {
        // Arrange
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> budgetService.getAllBudgets(principal));
    }

    // ============= saveBudgets() Tests =============

    @Test
    @DisplayName("POST budgets - success")
    void postBudgets() {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(appUser));
        when(categoryRepository.findByName(anyString())).thenAnswer(invocation -> {
            String name = invocation.getArgument(0);
            return switch (name) {
                case "Category 1" -> Optional.of(category1);
                case "Category 2" -> Optional.of(category2);
                default -> Optional.empty();
            };
        });

        ArgumentCaptor<CategoryBudget> budgetCaptor = ArgumentCaptor.forClass(CategoryBudget.class);

        // Act
        budgetService.saveBudgets(principal, budgets);

        // Assert
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(categoryRepository, times(2)).findByName(anyString());
        verify(budgetRepository, times(2)).save(budgetCaptor.capture());

        List<CategoryBudget> savedBudgets = budgetCaptor.getAllValues();
        assertEquals(2, savedBudgets.size());

        assertTrue(savedBudgets.stream()
                .anyMatch(b -> b.getCategory().getName().equals("Category 1") && b.getAmount() == 100.0));
        assertTrue(savedBudgets.stream()
                .anyMatch(b -> b.getCategory().getName().equals("Category 2") &&  b.getAmount() == 200.0));
    }

    @Test
    @DisplayName("POST budgets - user does NOT exist")
    void postBudgetsUserDoesNotExist() {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> budgetService.saveBudgets(principal, budgets));
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(budgetRepository, never()).save(any());
    }

    @Test
    @DisplayName("POST budget - category does NOT exist")
    void postBudgetsCategoryDoesNotExist() {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(appUser));
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> budgetService.saveBudgets(principal, budgets));
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(categoryRepository, times(1)).findByName(anyString());
        verify(budgetRepository, never()).save(any());
    }

    @Test
    @DisplayName("POST budget - negative amount set")
    void postBudgetsNegativeAmount() {
        // Arrange
        budgets.put("Category 1", -100.0);
        budgets.put("Category 2", -200.0);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(appUser));
        when(categoryRepository.findByName(anyString())).thenAnswer(invocation -> {
            String name = invocation.getArgument(0);
            return switch (name) {
                case "Category 1" -> Optional.of(category1);
                case "Category 2" -> Optional.of(category2);
                default -> Optional.empty();
            };
        });

        // Act
        budgetService.saveBudgets(principal, budgets);

        // Assert
        assertThrows(IllegalArgumentException.class, () -> budgetService.saveBudgets(principal, budgets));
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(budgetRepository, never()).save(any());
    }

    // ============= updateBudgets() Tests =============

    @Test
    @DisplayName("PUT budget - update amounts - success")
    void putBudgets() {
        // Arrange
        budgets.put("Category 1", 300.0);
        budgets.put("Category 2", 400.0);

        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(categoryRepository.findByName(anyString())).thenAnswer(invocation -> {
            String name = invocation.getArgument(0);
            return switch (name) {
                case "Category 1" -> Optional.of(category1);
                case "Category 2" -> Optional.of(category2);
                default -> Optional.empty();
            };
        });
        when(budgetRepository.getCategoryBudgetByAppUser_IdAndCategory_Id(eq(1L), any(Category.class).getId()))
                .thenAnswer(invocation -> {
                    Category category = invocation.getArgument(0);
                    return switch (category.getName()) {
                        case "Category 1" -> budget1;
                        case "Category 2" -> budget2;
                        default -> Optional.empty();
                    };
                });


        ArgumentCaptor<CategoryBudget> budgetCaptor = ArgumentCaptor.forClass(CategoryBudget.class);

        // Act
        budgetService.updateBudgets(principal, budgets);

        // Assert
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(categoryRepository, times(2)).findByName(anyString());
        verify(budgetRepository, times(2))
                .getCategoryBudgetByAppUser_IdAndCategory_Id(eq(1L), any(Category.class).getId());
        verify(budgetRepository, times(2)).save(budgetCaptor.capture());

        List<CategoryBudget> savedBudgets = budgetCaptor.getAllValues();
        assertEquals(2, savedBudgets.size());

        assertTrue(savedBudgets.stream()
                .anyMatch(b -> b.getAmount() == 300.0));
        assertTrue(savedBudgets.stream()
                .anyMatch(b -> b.getAmount() == 400.0));
    }

    @Test
    @DisplayName("PUT budget - update one item - success")
    void putBudgetsOneItemSuccess() {
        // Arrange
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(categoryRepository.findByName("Category 1")).thenReturn(Optional.of(category1));
        when(budgetRepository.findById(category1.getId())).thenReturn(Optional.of(budget1));

        budgets.put("Category 1", 300.0);
        budgets.remove("Category 2");

        ArgumentCaptor<CategoryBudget> budgetCaptor = ArgumentCaptor.forClass(CategoryBudget.class);

        // Act
        budgetService.updateBudgets(principal, budgets);

        // Assert
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(categoryRepository, times(1)).findByName(anyString());
        verify(budgetRepository, times(1)).findById(category1.getId());
        verify(budgetRepository, times(1)).save(budgetCaptor.capture());

        List<CategoryBudget> savedBudgets = budgetCaptor.getAllValues();
        assertEquals(1, savedBudgets.size());

        assertTrue(savedBudgets.stream()
                .anyMatch(b -> b.getAmount() == 300.0));
    }

    @Test
    @DisplayName("PUT budget - user does NOT exist")
    void putBudgetsUserDoesNotExist() {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        // Act @ Assert
        assertThrows(UsernameNotFoundException.class, () -> budgetService.updateBudgets(principal, budgets));
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(budgetRepository, never()).save(any());
    }

    @Test
    @DisplayName("POST budget - category does NOT exist")
    void putBudgetsCategoryDoesNotExist() {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(appUser));
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> budgetService.updateBudgets(principal, budgets));
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(budgetRepository, never()).save(any());
    }

    @Test
    @DisplayName("PUT budget - update with null value")
    void putBudgetsNullValue() {
        // Arrange
        Map<String, Double> nullBudgets = new HashMap<>();

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(appUser));

        // Act
        budgetService.updateBudgets(principal, nullBudgets);

        // Assert
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(categoryRepository, never()).findByName(anyString());
        verify(budgetRepository, never()).save(any());
    }

    @Test
    @DisplayName("PUT budget - update to negative value")
    void putBudgetsNegativeValue() {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(appUser));
        when(categoryRepository.findByName("Category 1")).thenReturn(Optional.of(category1));
        when(budgetRepository.getCategoryBudgetByAppUser_IdAndCategory_Id(eq(1L), any(Category.class).getId()))
                .thenReturn(budget1);

        budgets.put("Category 1", -300.0);
        budgets.remove("Category 2");

        ArgumentCaptor<CategoryBudget> budgetCaptor = ArgumentCaptor.forClass(CategoryBudget.class);

        // Act
        budgetService.updateBudgets(principal, budgets);

        // Assert
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(categoryRepository, times(1)).findByName(anyString());
        verify(budgetRepository, times(1))
                .getCategoryBudgetByAppUser_IdAndCategory_Id(eq(1L), any(Category.class).getId());
        verify(budgetRepository, never()).save(budgetCaptor.capture());

        List<CategoryBudget> savedBudgets = budgetCaptor.getAllValues();
        assertEquals(0, savedBudgets.size());
    }

    @Test
    @DisplayName("PUT budget - budget does NOT exist")
    void putBudgetsUserDoesNotOwnBudget() {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(appUser));
        when(categoryRepository.findByName("Category 1")).thenReturn(Optional.of(category1));
        when(budgetRepository.getCategoryBudgetByAppUser_IdAndCategory_Id(eq(1L), any(Category.class).getId()))
                .thenReturn(null);

        budgets.remove("Category 2");

        // Act & Assert
        assertThrows(CategoryBudgetNotFoundException.class, () -> budgetService.updateBudgets(principal, budgets));
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(categoryRepository, times(1)).findByName(anyString());
        verify(budgetRepository, times(1))
                .getCategoryBudgetByAppUser_IdAndCategory_Id(eq(1L), any(Category.class).getId());
        verify(budgetRepository, never()).save(any());
    }

    // ============= deleteBudgets() Tests =============

    @Test
    @DisplayName("DELETE budget - success")
    void deleteBudgets() {
        // Arrange
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(categoryRepository.findIdByName("Category 1")).thenReturn(category1.getId());
        when(budgetRepository.getCategoryBudgetByAppUser_IdAndCategory_Id(eq(1L), any(Category.class).getId()))
                .thenReturn(budget1);

        // Act
        budgetService.deleteBudgets(principal, "Category 1");

        // Assert
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(categoryRepository, times(1)).findIdByName("Category 1");
        verify(budgetRepository, times(1))
                .getCategoryBudgetByAppUser_IdAndCategory_Id(eq(1L), any(Category.class).getId());
        verify(budgetRepository, times(1)).delete(any(CategoryBudget.class));
    }

    @Test
    @DisplayName("DELETE budget - user does NOT exist")
    void deleteBudgetsUserDoesNotExist() {
        // Arrange
        when(userRepository.findIdByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> budgetService.deleteBudgets(principal, "Category 1"));
        verify(userRepository, times(1)).findIdByUsername("nonexistent");
        verify(budgetRepository, never()).delete(any(CategoryBudget.class));
    }

    @Test
    @DisplayName("DELETE budget - category does NOT exist")
    void deleteBudgetsCategoryDoesNotExist() {
        // Arrange
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(categoryRepository.findIdByName("nonexistent")).thenReturn(null);

        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> budgetService.deleteBudgets(principal, "nonexistent"));
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(categoryRepository, times(1)).findIdByName("nonexistent");
        verify(budgetRepository, never()).delete(any(CategoryBudget.class));
    }

    @Test
    @DisplayName("DELETE budget - budget does NOT exist")
    void deleteBudgetsNullValue() {
        // Arrange
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(categoryRepository.findIdByName("Category 1")).thenReturn(1L);
        when(budgetRepository.getCategoryBudgetByAppUser_IdAndCategory_Id(1L, 1L)).thenReturn(null);

        // Act & Assert
        assertThrows(CategoryBudgetNotFoundException.class,
                () -> budgetService.deleteBudgets(principal, "Category 1"));
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(categoryRepository, times(1)).findIdByName("Category 1");
        verify(budgetRepository, times(1)).getCategoryBudgetByAppUser_IdAndCategory_Id(1L, 1L);
        verify(budgetRepository, never()).delete(any(CategoryBudget.class));
    }

    @Test
    @DisplayName("DELETE budget - ignores null value")
    void deleteBudgetIgnoresNullValue() {
        // Arrange
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(categoryRepository.findIdByName(null)).thenReturn(null);

        // Act & Assert
        assertThrows(CategoryBudgetNotFoundException.class,
                () -> budgetService.deleteBudgets(principal, "Category 1"));
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(categoryRepository, times(1)).findIdByName(null);
        verify(budgetRepository, never()).delete(any(CategoryBudget.class));
    }
}