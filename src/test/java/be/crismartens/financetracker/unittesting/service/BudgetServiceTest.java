package be.crismartens.financetracker.unittesting.service;

import be.crismartens.financetracker.CategoryBudgetNotFoundException;
import be.crismartens.financetracker.CategoryNotFoundException;
import be.crismartens.financetracker.dto.CategoryBudgetDTO;
import be.crismartens.financetracker.model.AppUser;
import be.crismartens.financetracker.model.BudgetRequestBody;
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

    BudgetRequestBody budgets;

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

        budgets = new BudgetRequestBody("Category 1", 100.0);
    }

    // ============= getAllBudgets() Tests =============

    @Test
    @DisplayName("GET all budgets - success")
    void getAllBudgets() {
        // Arrange
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(budgetRepository.getCategoryBudgetByAppUser_Id(1L)).thenReturn(List.of(budget1, budget2));

        // Act
        List<CategoryBudgetDTO> result = budgetService.getAllBudgets(principal);

        // Assert
        assertEquals(budgets.getAmount(), result.get(0).getAmount());

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
        List<CategoryBudgetDTO> result = budgetService.getAllBudgets(principal);

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
    @DisplayName("POST budget - success")
    void postBudgets() {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(appUser));
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(category1));

        ArgumentCaptor<CategoryBudget> budgetCaptor = ArgumentCaptor.forClass(CategoryBudget.class);

        // Act
        budgetService.saveBudgets(principal, budgets);

        // Assert
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(categoryRepository, times(1)).findByName(anyString());
        verify(budgetRepository, times(1)).save(budgetCaptor.capture());

        List<CategoryBudget> savedBudgets = budgetCaptor.getAllValues();
        assertEquals(1, savedBudgets.size());

        assertTrue(savedBudgets.stream()
                .anyMatch(b -> b.getCategory().getName().equals("Category 1") && b.getAmount() == 100.0));
    }

    @Test
    @DisplayName("POST budgets - user does NOT exist")
    void postBudgetsUserDoesNotExist() {
        // Arrange
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
                return "nonexistent";
            }
        };
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> budgetService.saveBudgets(principal, budgets));
        verify(userRepository, times(1)).findByUsername("nonexistent");
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
        budgets.setAmount(-100.0);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(appUser));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> budgetService.saveBudgets(principal, budgets));
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(budgetRepository, never()).save(any());
    }

    // ============= updateBudgets() Tests =============

    @Test
    @DisplayName("PUT budget - update amounts - success")
    void putBudgets() {
        // Arrange
        budgets.setAmount(300.0);

        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(categoryRepository.findIdByName("Category 1")).thenReturn(1L);
        when(budgetRepository.getCategoryBudgetByAppUser_IdAndCategory_Id(1L, 1L))
                .thenReturn(budget1);

        ArgumentCaptor<CategoryBudget> budgetCaptor = ArgumentCaptor.forClass(CategoryBudget.class);

        // Act
        budgetService.updateBudgets(principal, budgets);

        // Assert
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(categoryRepository, times(1)).findIdByName("Category 1");
        verify(budgetRepository, times(1)).getCategoryBudgetByAppUser_IdAndCategory_Id(1L, 1L);
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
                return "nonexistent";
            }
        };
        when(userRepository.findIdByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act @ Assert
        assertThrows(UsernameNotFoundException.class, () -> budgetService.updateBudgets(principal, budgets));
        verify(userRepository, times(1)).findIdByUsername("nonexistent");
        verify(budgetRepository, never()).save(any());
    }

    @Test
    @DisplayName("PUT budget - category does NOT exist")
    void putBudgetsCategoryDoesNotExist() {
        // Arrange
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(categoryRepository.findIdByName(anyString())).thenReturn(null);

        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> budgetService.updateBudgets(principal, budgets));
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(categoryRepository, times(1)).findIdByName(anyString());
        verify(budgetRepository, never()).save(any());
    }

    @Test
    @DisplayName("PUT budget - update with null value")
    void putBudgetsNullValue() {
        // Arrange
        BudgetRequestBody nullBudget = new BudgetRequestBody("NullCategory", null);

        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> budgetService.updateBudgets(principal, nullBudget));
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(categoryRepository, never()).findByName(anyString());
        verify(budgetRepository, never()).save(any());
    }

    @Test
    @DisplayName("PUT budget - update to negative value")
    void putBudgetsNegativeValue() {
        // Arrange
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(eq(1L)));

        budgets.setAmount(-100.0);

        ArgumentCaptor<CategoryBudget> budgetCaptor = ArgumentCaptor.forClass(CategoryBudget.class);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> budgetService.updateBudgets(principal, budgets));
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(categoryRepository, never()).findByName(anyString());
        verify(budgetRepository, never())
                .getCategoryBudgetByAppUser_IdAndCategory_Id(1L, 1L);
        verify(budgetRepository, never()).save(budgetCaptor.capture());

        List<CategoryBudget> savedBudgets = budgetCaptor.getAllValues();
        assertEquals(0, savedBudgets.size());
    }

    @Test
    @DisplayName("PUT budget - budget does NOT exist")
    void putBudgetsUserDoesNotOwnBudget() {
        // Arrange
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(categoryRepository.findIdByName("Category 1")).thenReturn(1L);
        when(budgetRepository.getCategoryBudgetByAppUser_IdAndCategory_Id(1L, 1L))
                .thenReturn(null);

        // Act & Assert
        assertThrows(CategoryBudgetNotFoundException.class, () -> budgetService.updateBudgets(principal, budgets));
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(categoryRepository, times(1)).findIdByName("Category 1");
        verify(budgetRepository, times(1))
                .getCategoryBudgetByAppUser_IdAndCategory_Id(1L, 1L);
        verify(budgetRepository, never()).save(any());
    }

    // ============= deleteBudgets() Tests =============

    @Test
    @DisplayName("DELETE budget - success")
    void deleteBudgets() {
        // Arrange
        when(userRepository.findIdByUsername("testUser")).thenReturn(Optional.of(1L));
        when(categoryRepository.findIdByName("Category 1")).thenReturn(category1.getId());
        when(budgetRepository.getCategoryBudgetByAppUser_IdAndCategory_Id(eq(1L), anyLong()))
                .thenReturn(budget1);

        // Act
        budgetService.deleteBudgets(principal, "Category 1");

        // Assert
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(categoryRepository, times(1)).findIdByName("Category 1");
        verify(budgetRepository, times(1))
                .getCategoryBudgetByAppUser_IdAndCategory_Id(eq(1L), anyLong());
        verify(budgetRepository, times(1)).delete(any(CategoryBudget.class));
    }

    @Test
    @DisplayName("DELETE budget - user does NOT exist")
    void deleteBudgetsUserDoesNotExist() {
        // Arrange
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
                return "nonexistent";
            }
        };
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

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> budgetService.deleteBudgets(principal, null));
        verify(userRepository, times(1)).findIdByUsername("testUser");
        verify(categoryRepository, never()).findIdByName(anyString());
        verify(budgetRepository, never()).delete(any(CategoryBudget.class));
    }
}