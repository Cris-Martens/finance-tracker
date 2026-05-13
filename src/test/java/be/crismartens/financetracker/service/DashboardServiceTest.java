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

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    private UserDetails principal2;

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

        principal2 = new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of(new SimpleGrantedAuthority("ROLE_User"));
            }

            @Override
            public @Nullable String getPassword() {
                return "";
            }

            @Override
            public @Nullable String getUsername() {
                return "nonexistent@example.com";
            }
        };

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

        category1 = new Category(1L, "Hiusing");
        category2 = new Category(2L, "Utilities");
        category3 = new Category(3L, "Groceries");

        account =
                new AccountInfo("John", "Doe", "Belgium", 2400.0, user);
    }

    // ------- Get Latest Expenses Tests -------

    @Test
    @DisplayName("GET latest expenses - Success")
    public void getLatestExpenses() {}

    @Test
    @DisplayName("GET latest expenses - Empty")
    public void getLatestExpensesEmpty() {}

    @Test
    @DisplayName("GET latest expenses - Invalid User")
    public void getLatestExpensesInvalidUser() {}

    // ------- Get Expenses By Month Tests -------

    @Test
    @DisplayName("GET expenses by month - success")
    public void getExpensesByMonth() {}

    @Test
    @DisplayName("GET expenses by month - empty")
    public void getExpensesByMonthEmpty() {}

    @Test
    @DisplayName("GET expenses by month - invalid user")
    public void getExpensesByMonthInvalidUser() {}

    // ------- Get Remaining Budgets Tests -------

    @Test
    @DisplayName("Get Smallest Budgets Remaining - Success")
    public void getSmallestBudgetsRemaining() {}

    @Test
    @DisplayName("Get Smallest Budgets Remaining - No expenses")
    public void getSmallestBudgetsRemainingNoExpenses() {}

    @Test
    @DisplayName("Get Smallest Budgets Remaining - No Budgets")
    public void getSmallestBudgetsRemainingNoBudgets() {}

    @Test
    @DisplayName("Get Smallest Budgets Remaining - Invalid User")
    public void getSmallestBudgetsRemainingInvalidUser() {}

    // ------- Get Saved Amount Tests -------

    @Test
    @DisplayName("Get Saved Amount - Success")
    public void getSavedAmount() {}

    @Test
    @DisplayName("Get Saved Amount - No expenses")
    public void getSavedAmountNoExpenses() {}

    @Test
    @DisplayName("Get Saved Amount - No Income")
    public void getSavedAmountNoIncome() {}

    @Test
    @DisplayName("Get Saved Amount - Empty")
    public void getSavedAmountEmpty() {}

    @Test
    @DisplayName("Get Saved Amount - Invalid User")
    public void getSavedAmountInvalidUser() {}
}