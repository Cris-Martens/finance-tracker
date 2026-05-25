package be.crismartens.financetracker.unittesting.service;

import be.crismartens.financetracker.NoIncomeAddedException;
import be.crismartens.financetracker.dto.BudgetAndSpendDTO;
import be.crismartens.financetracker.dto.ExpenseDTO;
import be.crismartens.financetracker.model.*;
import be.crismartens.financetracker.repository.AccountRepository;
import be.crismartens.financetracker.repository.BudgetRepository;
import be.crismartens.financetracker.repository.ExpensesRepository;
import be.crismartens.financetracker.repository.UserRepository;
import be.crismartens.financetracker.service.DashboardService;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExpensesRepository expensesRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private DashboardService dashboardService;

    // Objects for tests
    private AppUser user;
    private UserDetails principal1;

    private Expense expense1;
    private Expense expense2;
    private Expense expense3;
    private Expense expense4;
    private Expense expense5;

    private CategoryBudget budget1;
    private CategoryBudget budget2;
    private CategoryBudget budget3;

    private Category category1;
    private Category category2;
    private Category category3;

    private AccountInfo account;

    @BeforeEach
    void setUp() {
        user = new AppUser();
        user.setUsername("test@example.com");
        user.setPassword("ValidPass123!");

        principal1 = new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of(new SimpleGrantedAuthority("ROLE_USER"));
            }

            @Override
            public @Nullable String getPassword() {
                return "";
            }

            @Override
            public String getUsername() {
                return "test@example.com";
            }
        };

        category1 = new Category(1L, "Housing");
        category2 = new Category(2L, "Utilities");
        category3 = new Category(3L, "Groceries");

        expense1 =
                new Expense(LocalDate.parse("2026-05-12"), category1, 750.0, "");
        expense2 =
                new Expense(LocalDate.parse("2026-05-13"), category2, 180.0, "");
        expense3 =
                new Expense(LocalDate.parse("2026-04-14"), category3, 44.97, "Snacks");
        expense4 =
                new Expense(LocalDate.parse("2026-03-15"), category2, 210.0, "");
        expense5 =
                new Expense(LocalDate.parse("2026-04-16"), category1, 750.0, "Rent");

        budget1 = new CategoryBudget(750.0, category1, user);
        budget2 = new CategoryBudget(200.0, category2, user);
        budget3 = new CategoryBudget(80.0, category3, user);


        account =
                new AccountInfo("John", "Doe", "Belgium", 2400.0, user);
    }

    // ------- Get Latest Expenses Tests -------

    @Test
    @DisplayName("GET latest expenses - Success")
    public void getLatestExpenses() {
        // Arrange
        List<ExpenseDTO> expenseDTOS = new ArrayList<>();
        expenseDTOS.add(new ExpenseDTO(expense1));
        expenseDTOS.add(new ExpenseDTO(expense2));
        expenseDTOS.add(new ExpenseDTO(expense3));
        expenseDTOS.add(new ExpenseDTO(expense4));
        expenseDTOS.add(new ExpenseDTO(expense5));

        when(userRepository.findIdByUsername(any())).thenReturn(Optional.of(1L));
        when(expensesRepository.findTop5ByAppUser_IdOrderByExpenseDateDesc(1L)).thenReturn(expenseDTOS);

        // Act
        List<ExpenseDTO> result = dashboardService.getLatestExpensesByAppUserId(principal1);

        // Assert
        assertEquals(expenseDTOS, result);
        verify(userRepository, times(1)).findIdByUsername(any());
        verify(expensesRepository, times(1)).findTop5ByAppUser_IdOrderByExpenseDateDesc(1L);
    }

    @Test
    @DisplayName("GET latest expenses - Empty")
    public void getLatestExpensesEmpty() {
        // Arrange
        List<ExpenseDTO> expenseDTOS = new ArrayList<>();

        when(userRepository.findIdByUsername(any())).thenReturn(Optional.of(1L));
        when(expensesRepository.findTop5ByAppUser_IdOrderByExpenseDateDesc(1L)).thenReturn(expenseDTOS);

        // Act
        List<ExpenseDTO> result = dashboardService.getLatestExpensesByAppUserId(principal1);

        // Assert
        assertEquals(expenseDTOS, result);
        verify(userRepository, times(1)).findIdByUsername(any());
        verify(expensesRepository, times(1)).findTop5ByAppUser_IdOrderByExpenseDateDesc(1L);
    }

    @Test
    @DisplayName("GET latest expenses - Invalid User")
    public void getLatestExpensesInvalidUser() {
        // Arrange
        when(userRepository.findIdByUsername(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> dashboardService.getLatestExpensesByAppUserId(principal1));
        verify(userRepository, times(1)).findIdByUsername(any());
        verify(expensesRepository, never()).findTop5ByAppUser_IdOrderByExpenseDateDesc(any());
    }

    // ------- Get Expenses By Month Tests -------

    @Test
    @DisplayName("GET expenses by month - success")
    public void getExpensesByMonth() {
        // Arrange
        Map<String, Double> expected = new LinkedHashMap<>();
        expected.put("MAY", 930.0);
        expected.put("APRIL", 794.97);
        expected.put("MARCH", 210.0);

        Object[] object1 = new Object[]{5, 930};
        Object[] object2 = new Object[]{4, 794.97};
        Object[] object3 = new Object[]{3, 210};

        List<Object[]> expenses = new ArrayList<>();
        expenses.add(object1);
        expenses.add(object2);
        expenses.add(object3);

        when(userRepository.findIdByUsername(any())).thenReturn(Optional.of(1L));
        when(expensesRepository.findTop12ByAppUser_IdGroupedByMonth(1L)).thenReturn(expenses);

        // Act
        Map<String, Double> result = dashboardService.getUserExpensesByMonth(principal1);

        // Assert
        assertEquals(expected, result);
        verify(userRepository, times(1)).findIdByUsername(any());
        verify(expensesRepository, times(1)).findTop12ByAppUser_IdGroupedByMonth(1L);
    }

    @Test
    @DisplayName("GET expenses by month - empty")
    public void getExpensesByMonthEmpty() {
        // Arrange
        Map<String, Double> expected = new LinkedHashMap<>();

        List<Object[]> expenses = new ArrayList<>();
        when(userRepository.findIdByUsername(any())).thenReturn(Optional.of(1L));
        when(expensesRepository.findTop12ByAppUser_IdGroupedByMonth(1L)).thenReturn(expenses);

        // Act
        Map<String, Double> result = dashboardService.getUserExpensesByMonth(principal1);

        // Assert
        assertEquals(expected, result);
        verify(userRepository, times(1)).findIdByUsername(any());
        verify(expensesRepository, times(1)).findTop12ByAppUser_IdGroupedByMonth(1L);
    }

    @Test
    @DisplayName("GET expenses by month - invalid user")
    public void getExpensesByMonthInvalidUser() {
        // Arrange
        when(userRepository.findIdByUsername(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> dashboardService.getUserExpensesByMonth(principal1));
        verify(userRepository, times(1)).findIdByUsername(any());
        verify(expensesRepository, never()).findTop12ByAppUser_IdGroupedByMonth(any());
    }

    // ------- Get Remaining Budgets Tests -------

    @Test
    @DisplayName("Get Smallest Budgets Remaining - Success")
    public void getSmallestBudgetsRemaining() {
        // Arrange
        List<CategoryBudget> budgets = new ArrayList<>();
        budgets.add(budget1);
        budgets.add(budget2);
        budgets.add(budget3);

        List<Object[]> expensesPerCategory = new ArrayList<>();
        expensesPerCategory.add(new Object[]{category1, 750.0});
        expensesPerCategory.add(new Object[]{category2, 180.0});

        when(userRepository.findIdByUsername(any())).thenReturn(Optional.of(1L));
        when(budgetRepository.getCategoryBudgetByAppUser_Id(1L)).thenReturn(budgets);
        when(expensesRepository.findExpensesPerCategoryByAppUser_IdAndThisMonth(eq(1L), any(), any()))
                .thenReturn(expensesPerCategory);

        // Act
        List<BudgetAndSpendDTO> result = dashboardService.getSmallestBudgetRemainders(principal1);

        // Assert
        List<BudgetAndSpendDTO> expected = new ArrayList<>();
        expected.add(new BudgetAndSpendDTO("Housing", 750.0, 750.0, 0.0));
        expected.add(new BudgetAndSpendDTO("Utilities", 200.0, 180.0, 20.0));
        expected.add(new BudgetAndSpendDTO("Groceries", 80.0, 0.0, 80.0));

        assertEquals(expected, result);
        verify(userRepository, times(1)).findIdByUsername(any());
        verify(budgetRepository, times(1)).getCategoryBudgetByAppUser_Id(1L);
        verify(expensesRepository, times(1))
                .findExpensesPerCategoryByAppUser_IdAndThisMonth(eq(1L), any(), any());
    }

    @Test
    @DisplayName("Get Smallest Budgets Remaining - No expenses")
    public void getSmallestBudgetsRemainingNoExpenses() {
        // Arrange
        List<CategoryBudget> budgets = new ArrayList<>();
        budgets.add(budget1);
        budgets.add(budget2);
        budgets.add(budget3);

        List<Object[]> expensesPerCategory = new ArrayList<>();

        when(userRepository.findIdByUsername(any())).thenReturn(Optional.of(1L));
        when(budgetRepository.getCategoryBudgetByAppUser_Id(1L)).thenReturn(budgets);
        when(expensesRepository.findExpensesPerCategoryByAppUser_IdAndThisMonth(eq(1L), any(), any()))
                .thenReturn(expensesPerCategory);

        // Act
        List<BudgetAndSpendDTO> result = dashboardService.getSmallestBudgetRemainders(principal1);

        // Assert
        List<BudgetAndSpendDTO> expected = new ArrayList<>();
        expected.add(new BudgetAndSpendDTO("Groceries", 80.0, 0.0, 80.0));
        expected.add(new BudgetAndSpendDTO("Utilities", 200.0, 0.0, 200.0));
        expected.add(new BudgetAndSpendDTO("Housing", 750.0, 0.0, 750.0));


        assertEquals(expected, result);
        verify(userRepository, times(1)).findIdByUsername(any());
        verify(budgetRepository, times(1)).getCategoryBudgetByAppUser_Id(1L);
        verify(expensesRepository, times(1))
                .findExpensesPerCategoryByAppUser_IdAndThisMonth(eq(1L), any(), any());
    }

    @Test
    @DisplayName("Get Smallest Budgets Remaining - No Budgets")
    public void getSmallestBudgetsRemainingNoBudgets() {
        // Arrange
        List<CategoryBudget> budgets = new ArrayList<>();
        when(userRepository.findIdByUsername(any())).thenReturn(Optional.of(1L));
        when(budgetRepository.getCategoryBudgetByAppUser_Id(1L)).thenReturn(budgets);

        // Act
        List<BudgetAndSpendDTO> result = dashboardService.getSmallestBudgetRemainders(principal1);

        // Arrange
        List<BudgetAndSpendDTO> expected = new ArrayList<>();
        assertEquals(expected, result);

        verify(userRepository, times(1)).findIdByUsername(any());
        verify(budgetRepository, times(1)).getCategoryBudgetByAppUser_Id(1L);
        verify(expensesRepository, never()).findExpensesPerCategoryByAppUser_IdAndThisMonth(eq(1L), any(), any());
    }

    @Test
    @DisplayName("Get Smallest Budgets Remaining - Invalid User")
    public void getSmallestBudgetsRemainingInvalidUser() {
        // Arrange
        when(userRepository.findIdByUsername(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> dashboardService.getSmallestBudgetRemainders(principal1));

        verify(userRepository, times(1)).findIdByUsername(any());
        verify(budgetRepository, never()).getCategoryBudgetByAppUser_Id(1L);
        verify(expensesRepository, never()).findExpensesPerCategoryByAppUser_IdAndThisMonth(eq(1L), any(), any());
    }

    // ------- Get Saved Amount Tests -------

    @Test
    @DisplayName("Get Saved Amount - Success")
    public void getSavedAmount() throws ExecutionException, InterruptedException {
        // Arrange
        when(userRepository.findIdByUsername(any())).thenReturn(Optional.of(1L));
        when(accountRepository.findMonthlyIncomeByAppUser_Id(1L)).thenReturn(2400.0);
        when(expensesRepository.getSumExpensesByAppUser_IdAndMonth(eq(1L), any(), any())).thenReturn(930.0);

        // Act
        Double result = dashboardService.getSavedAmount(principal1);

        // Assert
        double expected = 2400.0 - 930.0;
        assertEquals(expected, result);

        verify(userRepository, times(1)).findIdByUsername(any());
        verify(accountRepository, times(1)).findMonthlyIncomeByAppUser_Id(1L);
        verify(expensesRepository, times(1)).getSumExpensesByAppUser_IdAndMonth(eq(1L), any(), any());
    }

    @Test
    @DisplayName("Get Saved Amount - No expenses")
    public void getSavedAmountNoExpenses() throws ExecutionException, InterruptedException {
        // Arrange
        when(userRepository.findIdByUsername(any())).thenReturn(Optional.of(1L));
        when(accountRepository.findMonthlyIncomeByAppUser_Id(1L)).thenReturn(2400.0);
        when(expensesRepository.getSumExpensesByAppUser_IdAndMonth(eq(1L), any(), any())).thenReturn(0.0);

        // Act
        Double result = dashboardService.getSavedAmount(principal1);

        // Assert
        double expected = 2400.0;
        assertEquals(expected, result);
        verify(userRepository, times(1)).findIdByUsername(any());
        verify(accountRepository, times(1)).findMonthlyIncomeByAppUser_Id(1L);
        verify(expensesRepository, times(1))
                .getSumExpensesByAppUser_IdAndMonth(eq(1L), any(), any());
    }

    @Test
    @DisplayName("Get Saved Amount - No Income")
    public void getSavedAmountNoIncome() throws ExecutionException, InterruptedException {
        // Arrange

        when(userRepository.findIdByUsername(any())).thenReturn(Optional.of(1L));
        when(accountRepository.findMonthlyIncomeByAppUser_Id(1L)).thenReturn(null);

        // Act & Assert
        assertThrows(NoIncomeAddedException.class, () -> dashboardService.getSavedAmount(principal1));

        verify(userRepository, times(1)).findIdByUsername(any());
        verify(accountRepository, times(1)).findMonthlyIncomeByAppUser_Id(1L);
        verify(expensesRepository, never()).getSumExpensesByAppUser_IdAndMonth(any(), any(), any());
    }

    @Test
    @DisplayName("Get Saved Amount - Invalid User")
    public void getSavedAmountInvalidUser() {
        // Arrange
        when(userRepository.findIdByUsername(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> dashboardService.getSavedAmount(principal1));
        verify(userRepository, times(1)).findIdByUsername(any());
        verify(accountRepository, never()).findMonthlyIncomeByAppUser_Id(1L);
        verify(expensesRepository, never()).getSumExpensesByAppUser_IdAndMonth(any(), any(), any());
    }
}