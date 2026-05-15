package be.crismartens.financetracker.unittesting.controller;

import be.crismartens.financetracker.NullValueException;
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

import java.util.LinkedHashMap;
import java.util.Map;

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
    Map<String, Double> oneBudget = new LinkedHashMap<>();

    Map<String, Double> budgetList = new LinkedHashMap<>();

    Map<String, Double> nullBudget = new LinkedHashMap<>();

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                    .webAppContextSetup(context)
                    .build();

        oneBudget.put("Housing", 850.0);

        budgetList.put("Housing", 850.0);
        budgetList.put("Utilities", 200.0);
        budgetList.put("Groceries", 400.0);

        nullBudget.put("Housing", null);
    }

    // ------ Insert Budgets Tests ------

    @Test
    @WithMockUser
    @DisplayName("Insert one budget - Success")
    void insertBudgetSuccess() throws Exception {
        // Arrange
        doNothing().when(budgetService).saveBudgets(any(), any());

        // Act & Assert
        mockMvc.perform(post("/api/v1/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oneBudget))
                .with(csrf()))
                .andExpect(status().isOk());

        verify(budgetService, times(1)).saveBudgets(any(), any());
    }

    @Test
    @WithMockUser
    @DisplayName("Insert multiple budgets - Success")
    void insertMultipleBudgetSuccess() throws Exception {
        // Arrange
        doNothing().when(budgetService).saveBudgets(any(), any());

        // Act & Assert
        mockMvc.perform(post("/api/v1/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(budgetList))
                .with(csrf()))
                .andExpect(status().isOk());

        verify(budgetService, times(1)).saveBudgets(any(), any());
    }

    @Test
    @WithMockUser
    @DisplayName("Insert Budgets - Handle Null Values")
    void handleNullValues() throws Exception {
        // Arrange
        doThrow(new NullValueException("Unable to insert Null value"))
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
    @DisplayName("Insert Budgets - Empty List")
    void handleEmptyList() throws Exception {
        // Arrange
        doNothing().when(budgetService).saveBudgets(any(), any());

        Map<String, Double> emptyBudget = new LinkedHashMap<>();

        // Act & Assert
        mockMvc.perform(post("/api/v1/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyBudget))
                .with(csrf()))
                .andExpect(status().isOk());

        verify(budgetService, times(1)).saveBudgets(any(), any());
    }

    @Test
    @DisplayName("Insert Budgets - Unauthorized")
    void handleNonexistentUser() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(budgetList))
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
        when(budgetService.getAllBudgets(any())).thenReturn(budgetList);

        // Act & Assert
        mockMvc.perform(get("/api/v1/budget")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Housing").value(850.0))
                .andExpect(jsonPath("$.Utilities").value(200.0))
                .andExpect(jsonPath("$.Groceries").value(400.0));

        verify(budgetService, times(1)).getAllBudgets(any());
    }

    @Test
    @WithMockUser
    @DisplayName("Get Budgets - Empty List")
    void getBudgetEmptyList() throws Exception {
        // Arrange
        Map<String, Double> emptyBudget = new LinkedHashMap<>();

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
        doNothing().when(budgetService).updateBudgets(any(), any());

        // Act & Assert
        mockMvc.perform(put("/api/v1/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(budgetList))
                .with(csrf()))
                .andExpect(status().isOk());

        verify(budgetService, times(1)).updateBudgets(any(), any());
    }

    @Test
    @WithMockUser
    @DisplayName("Update Budget - Empty List")
    void updateBudgetEmptyList() throws Exception {
        // Arrange
        doNothing().when(budgetService).updateBudgets(any(), any());

        Map<String, Double> emptyBudget = new LinkedHashMap<>();

        // Act & Assert
        mockMvc.perform(put("/api/v1/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyBudget))
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(budgetService, times(1)).updateBudgets(any(), any());
    }

    @Test
    @WithMockUser
    @DisplayName("Update Budget - With Null Values")
    void updateBudgetWithNullValues() throws Exception {
        // Arrange
        doNothing().when(budgetService).updateBudgets(any(), any());

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
                .content(objectMapper.writeValueAsString(budgetList))
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
        mockMvc.perform(delete("/api/v1/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString("existent"))
                .with(csrf()))
                .andExpect(status().isOk());

        verify(budgetService, times(1)).deleteBudgets(any(), any());
    }

    @Test
    @WithMockUser
    @DisplayName("Delete Budget - Budget Not Found")
    void deleteBudgetNotFound() throws Exception {
        // Arrange
        doNothing().when(budgetService).deleteBudgets(any(), any());

        // Act & Assert
        mockMvc.perform(delete("/api/v1/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString("nonexistent"))
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(budgetService, times(1)).deleteBudgets(any(), any());
    }

    @Test
    @DisplayName("Delete Budget - Unauthorized")
    void deleteBudgetUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString("unauthorized"))
                .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(budgetService,never()).deleteBudgets(any(), any());
    }
}