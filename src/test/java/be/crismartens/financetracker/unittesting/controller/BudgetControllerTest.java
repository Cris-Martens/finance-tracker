package be.crismartens.financetracker.unittesting.controller;

import be.crismartens.financetracker.exceptions.CategoryNotFoundException;
import be.crismartens.financetracker.exceptions.NullValueException;
import be.crismartens.financetracker.dto.CategoryBudgetDTO;
import be.crismartens.financetracker.model.AppUser;
import be.crismartens.financetracker.model.BudgetRequestBody;
import be.crismartens.financetracker.model.Category;
import be.crismartens.financetracker.model.CategoryBudget;
import be.crismartens.financetracker.service.BudgetService;
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
import tools.jackson.databind.ObjectMapper;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Budget Controller Tests")
class BudgetControllerTest {

    @MockitoBean
    private BudgetService budgetService;

    @Autowired
    private WebApplicationContext context;

    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    // Initiate needed objects
    BudgetRequestBody oneBudget;
    BudgetRequestBody nullBudget;

    List<CategoryBudget> categoryBudgets;

    AppUser user;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                    .webAppContextSetup(context)
                    .build();

        user = new AppUser("test@example.com", "ValidPass123!", "ROLE_USER");

        oneBudget = new BudgetRequestBody("Housing", 850.0);
        nullBudget = new BudgetRequestBody("Housing", null);

        categoryBudgets = new ArrayList<>();
        categoryBudgets.add(new CategoryBudget(850.0, new Category("Housing"), user));
        categoryBudgets.add(new CategoryBudget(200.0, new Category("Utilities"), user));
        categoryBudgets.add(new CategoryBudget(400.0, new Category("Groceries"), user));

    }

    // ------ Insert Budgets Tests ------

    @Test
    @WithMockUser
    @DisplayName("Insert one budget - Success")
    void insertBudgetSuccess() throws Exception {
        // Arrange
        CategoryBudget budget = categoryBudgets.get(0);
        when(budgetService.saveBudgets(any(), any())).thenReturn(new CategoryBudgetDTO(budget));

        // Act & Assert
        mockMvc.perform(post("/api/v1/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oneBudget))
                .with(csrf()))
                .andExpect(status().isCreated());

        verify(budgetService, times(1)).saveBudgets(any(), any());
    }

    @Test
    @WithMockUser
    @DisplayName("Insert Budgets - Handle Null Values")
    void handleNullValues() throws Exception {
        // Arrange
        doThrow(new NullValueException())
                .when(budgetService).saveBudgets(any(), any());

        // Act & Assert
        mockMvc.perform(post("/api/v1/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nullBudget))
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(budgetService, times(1)).saveBudgets(any(), any());
    }

    @Test
    @WithMockUser
    @DisplayName("Insert Budget - Empty")
    void handleEmptyList() throws Exception {
        // Arrange
        CategoryBudgetDTO budget = null;

        when(budgetService.saveBudgets(any(), any()))
                .thenReturn(budget);

        Map<String, Double> emptyBudget = new LinkedHashMap<>();

        // Act & Assert
        mockMvc.perform(post("/api/v1/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyBudget))
                .with(csrf()))
                .andExpect(status().isCreated());

        verify(budgetService, times(1)).saveBudgets(any(), any());
    }

    @Test
    @DisplayName("Insert Budget - Unauthorized")
    void handleNonexistentUser() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oneBudget))
                .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(budgetService, never()).saveBudgets(any(), any());
    }

    // ------ List Budgets Tests ------

    @Test
    @WithMockUser
    @DisplayName("Get Budgets - Success")
    void getBudgetSuccess() throws Exception {
        // arrange
        List<CategoryBudgetDTO> budgets = new ArrayList<>();
        for (CategoryBudget budget : categoryBudgets) {
            budgets.add(new CategoryBudgetDTO(budget));
        }
        when(budgetService.getAllBudgets(any())).thenReturn(budgets);

        // Act & Assert
        mockMvc.perform(get("/api/v1/budget")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].category_name").value("Housing"))
                .andExpect(jsonPath("$[0].amount").value(850.0));

        verify(budgetService, times(1)).getAllBudgets(any());
    }

    @Test
    @WithMockUser
    @DisplayName("Get Budgets - Empty List")
    void getBudgetEmptyList() throws Exception {
        // Arrange
        List<CategoryBudgetDTO> emptyBudget = new ArrayList<>();

        when(budgetService.getAllBudgets(any())).thenReturn(emptyBudget);

        // Act & Assert
        mockMvc.perform(get("/api/v1/budget")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(budgetService, times(1)).getAllBudgets(any());
    }

    @Test
    @DisplayName("Get Budgets - Unauthorized")
    void getBudgetNonexistentUser() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/budget")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(budgetService, never()).getAllBudgets(any());
    }

    // ------ Update Budgets Tests ------

    @Test
    @WithMockUser
    @DisplayName("Update Budget - Success")
    void updateBudgetSuccess() throws Exception {
        // Arrange
        CategoryBudgetDTO budget = new CategoryBudgetDTO(categoryBudgets.get(0));

        when(budgetService.updateBudgets(any(), any()))
                .thenReturn(budget);

        // Act & Assert
        mockMvc.perform(put("/api/v1/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oneBudget))
                .with(csrf()))
                .andExpect(status().isOk());

        verify(budgetService, times(1)).updateBudgets(any(), any());
    }

    @Test
    @WithMockUser
    @DisplayName("Update Budget - Empty List")
    void updateBudgetEmptyList() throws Exception {
        // Arrange
        CategoryBudgetDTO budget = null;

        when(budgetService.updateBudgets(any(), any()))
                .thenReturn(budget);

        Map<String, Double> emptyBudget = new LinkedHashMap<>();

        // Act & Assert
        mockMvc.perform(put("/api/v1/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyBudget))
                .with(csrf()))
                .andExpect(status().isOk());

        verify(budgetService, times(1)).updateBudgets(any(), any());
    }

    @Test
    @WithMockUser
    @DisplayName("Update Budget - With Null Values")
    void updateBudgetWithNullValues() throws Exception {
        // Arrange
        doThrow(NullValueException.class).when(budgetService).updateBudgets(any(), any());

        // Act & Assert
        mockMvc.perform(put("/api/v1/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nullBudget))
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(budgetService, times(1)).updateBudgets(any(), any());
    }

    @Test
    @DisplayName("Update Budget - Unauthorized")
    void updateBudgetNonexistentUser() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/v1/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oneBudget))
                .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(budgetService, never()).updateBudgets(any(), any());
    }

    // ------ Delete Budgets Tests ------

    @Test
    @WithMockUser
    @DisplayName("Delete Budget - Success")
    void deleteBudgetSuccess() throws Exception {
        // Arrange
        doNothing().when(budgetService).deleteBudgets(any(), any());

        // Act & Assert
        mockMvc.perform(delete("/api/v1/budget/existent")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(budgetService, times(1)).deleteBudgets(any(), any());
    }

    @Test
    @WithMockUser
    @DisplayName("Delete Budget - Budget Not Found")
    void deleteBudgetNotFound() throws Exception {
        // Arrange
        doThrow(CategoryNotFoundException.class).when(budgetService).deleteBudgets(any(), any());

        // Act & Assert
        mockMvc.perform(delete("/api/v1/budget/nonexistent")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(budgetService, times(1)).deleteBudgets(any(), any());
    }

    @Test
    @DisplayName("Delete Budget - Unauthorized")
    void deleteBudgetUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/budget/existent")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(budgetService,never()).deleteBudgets(any(), any());
    }
}