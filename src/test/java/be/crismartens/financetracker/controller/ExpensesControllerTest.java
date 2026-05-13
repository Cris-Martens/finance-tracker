package be.crismartens.financetracker.controller;

import be.crismartens.financetracker.EmptyExpenseException;
import be.crismartens.financetracker.ExpenseNotFoundException;
import be.crismartens.financetracker.dto.ExpenseDTO;
import be.crismartens.financetracker.model.Category;
import be.crismartens.financetracker.model.Expense;
import be.crismartens.financetracker.service.ExpensesService;
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
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("ExpenseController Tests")
class ExpensesControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private ExpensesService expensesService;


    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private Expense expense1;
    private Expense expense2;
    private Expense expense3;

    @BeforeEach
    void setUp() {
        // Build MockMvc with spring Security explicitly applied
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity()).build();

        objectMapper = new ObjectMapper();

        expense1 = new Expense();
        expense1.setId(1L);
        expense1.setExpenseDate(LocalDate.parse("2026-04-28"));
        expense1.setAmount(180.00);
        expense1.setCategory(new Category("Utilities"));
        expense1.setDescription("Electricity");

        expense2 = new Expense();
        expense2.setId(2L);
        expense2.setExpenseDate(LocalDate.parse("2026-04-29"));
        expense2.setAmount(91.78);
        expense2.setCategory(new Category("Groceries"));

        expense3 = new Expense();
        expense3.setId(3L);
        expense3.setExpenseDate(LocalDate.parse("2026-04-30"));
        expense3.setAmount(34.99);
        expense3.setCategory(new Category("Sports"));
        expense3.setDescription("Gym");
    }

    // =========== GET /api/v1/user/expenses ===========

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    @DisplayName("GET all expenses per user - success")
    void getAllExpensesPerUser() throws Exception {
        // Arrange
        List<ExpenseDTO> expenses = new ArrayList<>();
        expenses.add(new ExpenseDTO(expense1));
        expenses.add(new ExpenseDTO(expense2));
        expenses.add(new ExpenseDTO(expense3));

        when(expensesService.getExpensesByAppUserId(any())).thenReturn(expenses);

        // Act & Assert
        mockMvc.perform(get("/api/v1/user/expenses")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Electricity"))
                .andExpect(jsonPath("$[0].amount").value(180.00))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[2].id").value(3L));

        verify(expensesService, times(1)).getExpensesByAppUserId(any());
    }

    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    @DisplayName("GET all expenses per user - no exppenses")
    void getAllExpensesPerUserNoExpenses() throws Exception {
        // Arrange
        List<ExpenseDTO> expenses = new ArrayList<>();

        when(expensesService.getExpensesByAppUserId(any())).thenReturn(expenses);

        // Act & Assert
        mockMvc.perform(get("/api/v1/user/expenses")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk());

        verify(expensesService, times(1)).getExpensesByAppUserId(any());
    }

    @Test
    @DisplayName("GET all expenses - unauthorized")
    void getAllExpensesUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/user/expenses"))
                .andExpect(status().isUnauthorized());

        verify(expensesService, never()).getExpensesByAppUserId(any());
    }

    // =========== GET /api/v1/user/expenses/{id} ===========

    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    @DisplayName("GET expense by id - success")
    void getExpenseById() throws Exception {
        // Arrange
        ExpenseDTO expenseDTO = new ExpenseDTO(expense1);

        when(expensesService.getExpenseById(anyLong(), any())).thenReturn(expenseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/user/expenses/1")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Electricity"));

        verify(expensesService, times(1)).getExpenseById(anyLong(), any());
    }

    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    @DisplayName("GET expense by id - expense not found")
    void getExpenseByIdNotFound() throws Exception {
        // Arrange
        doThrow(ExpenseNotFoundException.class)
                .when(expensesService).getExpenseById(anyLong(), any());

        // Act & Assert
        mockMvc.perform(get("/api/v1/user/expenses/1")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(status().isNotFound());

        verify(expensesService, times(1)).getExpenseById(eq(1L), any());
    }

    @Test
    @DisplayName("GET expense by id - unauthorized")
    void getExpenseByIdUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/user/expenses/1"))
                .andExpect(status().isUnauthorized());

        verify(expensesService, never()).getExpenseById(anyLong(), any());
    }

    // =========== POST /api/v1/user/expenses ===========

    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    @DisplayName("POST expense - success")
    void addExpenseAuthorised() throws Exception {
        // Arrange
        doNothing().when(expensesService).addExpense(any(Expense.class), any());

        Expense testExpense = new Expense();
        testExpense.setExpenseDate(LocalDate.parse("2026-04-28"));
        testExpense.setAmount(91.78);
        testExpense.setCategory(new Category("Utilities"));
        testExpense.setDescription("Electricity");

        // Act & Assert
        mockMvc.perform(post("/api/v1/user/expenses")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testExpense))
                .with(csrf()))
                .andExpect(status().isOk());

        verify(expensesService, times(1)).addExpense(any(Expense.class), any());
    }

    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    @DisplayName("POST empty expense authorized")
    void addExpenseAuthorisedEmpty() throws Exception {
        // Arrange
        doThrow(EmptyExpenseException.class)
                .when(expensesService).addExpense(any(Expense.class), any());

        Expense testExpense = new Expense();

        // Act & Assert
        mockMvc.perform(post("/api/v1/user/expenses")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testExpense))
                .with(csrf()))
                .andExpect(status().isBadRequest());
        verify(expensesService, times(1)).addExpense(any(Expense.class), any());
    }

    @Test
    @DisplayName("POST expense - unauthorized")
    void addExpenseUnauthorized() throws Exception {
        // Arrange
        doNothing().when(expensesService).addExpense(any(Expense.class), any());

        Expense testExpense = new Expense();
        testExpense.setExpenseDate(LocalDate.parse("2026-04-28"));
        testExpense.setAmount(91.78);
        testExpense.setCategory(new Category("Utilities"));
        testExpense.setDescription("Electricity");

        // Act & Assert
        mockMvc.perform(post("/api/v1/user/expenses")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testExpense))
                .with(csrf()))
                .andExpect(status().isUnauthorized());
        verify(expensesService, never()).addExpense(any(Expense.class), any());
    }

    // =========== PUT /api/v1/user/expenses/{id} ===========

    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    @DisplayName("PUT update expense - success")
    void updateExpenseSuccess() throws Exception {
        // Arrange
        doNothing().when(expensesService).updateExpense(any(Expense.class), any());

        Expense updateExpense = new Expense();
        updateExpense.setId(1L);
        updateExpense.setExpenseDate(LocalDate.parse("2026-04-29"));
        updateExpense.setAmount(64.95);

        // Act & Assert
        mockMvc.perform(put("/api/v1/user/expenses")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateExpense))
                .with(csrf()))
                .andExpect(status().isOk());
        verify(expensesService, times(1)).updateExpense(any(Expense.class), any());
    }

    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    @DisplayName("PUT expense - not found")
    void updateExpenseNotFound() throws Exception {
        // Arrange
        doThrow(new ExpenseNotFoundException("Expense not found"))
                .when(expensesService).updateExpense(any(Expense.class), any());

        Expense updateExpense = new Expense();
        updateExpense.setId(5L);
        updateExpense.setExpenseDate(LocalDate.parse("2026-04-29"));
        updateExpense.setAmount(64.95);

        // Act & Assert
        mockMvc.perform(put("/api/v1/user/expenses")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateExpense))
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(expensesService, times(1)).updateExpense(any(Expense.class), any());
    }

    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    @DisplayName("PUT expense -  empty values")
    void updateExpenseEmptyValues() throws Exception {
        // Arrange
        doNothing().when(expensesService).addExpense(any(Expense.class), any());

        Expense updateExpense = new Expense();

        // Act & Assert
        mockMvc.perform(put("/api/v1/user/expenses")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateExpense))
                .with(csrf()))
                .andExpect(status().isOk());

        verify(expensesService, times(1)).updateExpense(any(Expense.class), any());
    }

    @Test
    @DisplayName("PUT expense - unauthorised")
    void updateExpenseUnauthorized() throws Exception {
        // Arrange
        doNothing().when(expensesService).addExpense(any(Expense.class), any());

        Expense updateExpense = new Expense();
        updateExpense.setId(1L);
        updateExpense.setExpenseDate(LocalDate.parse("2026-04-29"));
        updateExpense.setAmount(64.95);

        // Act & Assert
        mockMvc.perform(put("/api/v1/user/expenses")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateExpense))
                .with(csrf()))
                .andExpect(status().isUnauthorized());
        verify(expensesService, never()).updateExpense(any(Expense.class), any());
    }

    // =========== DELETE /api/v1/user/expenses/{id} ===========

    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    @DisplayName("DELETE expense - success")
    void deleteExpenseSuccess() throws Exception {
        // Arrange
        doNothing().when(expensesService).deleteExpense(any(Expense.class), any());

        Expense deleteExpense = new Expense();
        deleteExpense.setId(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/user/expenses")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteExpense))
                .with(csrf()))
                .andExpect(status().isOk());
        verify(expensesService, times(1)).deleteExpense(any(Expense.class), any());
    }

    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    @DisplayName("DELETE expense - not found")
    void deleteExpenseNotFound() throws Exception {
        // Arrange
        doThrow(new ExpenseNotFoundException("Expense not found"))
                .when(expensesService).deleteExpense(any(Expense.class), any());

        Expense deleteExpense = new Expense();
        deleteExpense.setId(5L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/user/expenses")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteExpense))
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(expensesService, times(1)).deleteExpense(any(Expense.class), any());
    }

    @Test
    @DisplayName("DELETE expense - unauthorized")
    void deleteExpenseUnauthorized() throws Exception {
        // Arrange
        Expense deleteExpense = new Expense();
        deleteExpense.setId(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/user/expenses")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteExpense))
                .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(expensesService, never()).deleteExpense(any(Expense.class), any());
    }
}