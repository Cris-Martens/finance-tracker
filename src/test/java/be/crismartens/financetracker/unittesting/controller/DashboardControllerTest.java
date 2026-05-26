package be.crismartens.financetracker.unittesting.controller;

import be.crismartens.financetracker.dto.BudgetAndSpendDTO;
import be.crismartens.financetracker.dto.ExpenseDTO;
import be.crismartens.financetracker.exceptions.NoIncomeAddedException;
import be.crismartens.financetracker.model.Category;
import be.crismartens.financetracker.model.Expense;
import be.crismartens.financetracker.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Dashboard Controller Tests")
class DashboardControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private DashboardService dashboardService;

    private MockMvc mockMvc;

    List<ExpenseDTO> expenseList;

    Map<String, Double> expensesByMonth;

    List<BudgetAndSpendDTO> budgetAndSpendList;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        expenseList = new ArrayList<>();
        expenseList.add(new ExpenseDTO(
                new Expense(LocalDate.parse("2026-04-15"), new Category(1L, "Housing"), 850.0, "")));
        expenseList.add(new ExpenseDTO(
                new Expense(LocalDate.parse("2026-04-16"), new Category(2L, "Utilities"), 200.0, "Electricity")));

        expensesByMonth = new LinkedHashMap<>();
        expensesByMonth.put("MARCH", 1600.0);
        expensesByMonth.put("APRIL", 1435.70);

        budgetAndSpendList = new ArrayList<>();
        budgetAndSpendList.add(new BudgetAndSpendDTO("Housing", 850.0, 850.0, 0.0));
        budgetAndSpendList.add(new BudgetAndSpendDTO("Utilities", 250.0, 200.0, 50.0));
    }

    // ========= List Latest Expenses DTO Tests =========

    @Test
    @WithMockUser
    @DisplayName("List Latest Expenses - Success")
    void listLatestExpensesSuccess() throws Exception {
        // Arrange
        when(dashboardService.getLatestExpensesByAppUserId(any())).thenReturn(expenseList);

        // Act & Assert
        mockMvc.perform(get("/api/v1/dashboard/latest-expenses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].amount").value(850.0))
                .andExpect(jsonPath("$[1].amount").value(200.0));

        verify(dashboardService, times(1)).getLatestExpensesByAppUserId((any()));
    }

    @Test
    @WithMockUser
    @DisplayName("List latest Expenses - Empty List")
    void listLatestExpensesEmptyList() throws Exception {
        // Arrange
        List<ExpenseDTO> emptyList = new ArrayList<>();

        when(dashboardService.getLatestExpensesByAppUserId(any())).thenReturn(emptyList);

        // Act & Assert
        mockMvc.perform(get("/api/v1/dashboard/latest-expenses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(dashboardService, times(1)).getLatestExpensesByAppUserId((any()));
    }

    @Test
    @DisplayName("List lastest Expenses - Unauthorized")
    void listLastestExpensesUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/dashboard/lastest-expenses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(dashboardService, never()).getLatestExpensesByAppUserId((any()));
    }

    // ========= List Expenses By Month Tests =========

    @Test
    @WithMockUser
    @DisplayName("List Expenses By Month - Success")
    void listExpensesByMonthSuccess() throws Exception {
        // Arrange
        when(dashboardService.getUserExpensesByMonth(any())).thenReturn(expensesByMonth);

        // Act & Assert
        mockMvc.perform(get("/api/v1/dashboard/expenses-by-month")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.MARCH").value(1600.0))
                .andExpect(jsonPath("$.APRIL").value(1435.7));

        verify(dashboardService, times(1)).getUserExpensesByMonth((any()));
    }

    @Test
    @WithMockUser
    @DisplayName("List Expenses By Month - Empty List")
    void listExpensesByMonthEmptyList() throws Exception {
        // Arrange
        Map<String, Double> emptyMap = new LinkedHashMap<>();

        when(dashboardService.getUserExpensesByMonth(any())).thenReturn(emptyMap);

        // Act & Assert
        mockMvc.perform(get("/api/v1/dashboard/expenses-by-month")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$").isEmpty());

        verify(dashboardService, times(1)).getUserExpensesByMonth((any()));
    }

    @Test
    @DisplayName("List Expenses By Month - Unauthorized")
    void listExpensesByMonthUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/dashboard/expenses-by-month")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(dashboardService, never()).getUserExpensesByMonth((any()));
    }

    // ========= List Budgets Left per Category Tests =========

    @Test
    @WithMockUser
    @DisplayName("List Budgets Left - Success")
    void listBudgetsLeftSuccess() throws Exception {
        // Arrange
        when(dashboardService.getSmallestBudgetRemainders(any())).thenReturn(budgetAndSpendList);

        // Act & Assert
        mockMvc.perform(get("/api/v1/dashboard/category-budget-left")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].category").value("Housing"))
                .andExpect(jsonPath("$[1].category").value("Utilities"))
                .andExpect(jsonPath("$[0].budget").value(850.0))
                .andExpect(jsonPath("$[0].spend").value(850.0))
                .andExpect(jsonPath("$[0].remaining").value(0.0));

        verify(dashboardService, times(1)).getSmallestBudgetRemainders(any());
    }

    @Test
    @WithMockUser
    @DisplayName("List Budgets Left - Empty List")
    void listBudgetsLeftEmptyList() throws Exception {
        // Arrange
        List<BudgetAndSpendDTO> emptyList = new ArrayList<>();

        when(dashboardService.getSmallestBudgetRemainders(any())).thenReturn(emptyList);

        // Act & Assert
        mockMvc.perform(get("/api/v1/dashboard/category-budget-left")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",  hasSize(0)));

        verify(dashboardService, times(1)).getSmallestBudgetRemainders(any());
    }

    @Test
    @DisplayName("List Budgets Left - Unauthorized")
    void listBudgetsLeftUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/dashboard/category-budget-left")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(dashboardService, never()).getSmallestBudgetRemainders(any());
    }

    // ========= List Total Saved Amount Tests =========

    @Test
    @WithMockUser
    @DisplayName("Get Total Saved Amount - Success")
    void getTotalSavedAmountSuccess() throws Exception {
        // Arrange
        when(dashboardService.getSavedAmount(any())).thenReturn(350.55);

        // Act & Assert
        mockMvc.perform(get("/api/v1/dashboard/totalsaved")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(350.55));

        verify(dashboardService, times(1)).getSavedAmount((any()));
    }

    @Test
    @WithMockUser
    @DisplayName("Get Total Saved Amount - Empty")
    void getTotalSavedAmountEmptyList() throws Exception {
        // Arrange
        doThrow(NoIncomeAddedException.class).when(dashboardService).getSavedAmount((any()));

        // Act & Assert
        mockMvc.perform(get("/api/v1/dashboard/totalsaved")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(dashboardService, times(1)).getSavedAmount((any()));
    }

    @Test
    @DisplayName("Get Total Saved Amount - Unauthorized")
    void getTotalSavedAmountUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/dashboard/totalsaved")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(dashboardService, never()).getSavedAmount((any()));
    }
}