package be.crismartens.financetracker.unittesting.controller;

import be.crismartens.financetracker.ExpenseNotFoundException;
import be.crismartens.financetracker.dto.AccountInfoDTO;
import be.crismartens.financetracker.model.AccountInfo;
import be.crismartens.financetracker.service.AccountService;
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

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("AccountController Tests")
class AccountControllerTest {

    @Autowired
    private WebApplicationContext webContext;

    @MockitoBean
    private AccountService accountService;

    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private AccountInfo accountInfo;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webContext)
                .apply(springSecurity()).build();

        objectMapper = new ObjectMapper();

        accountInfo = new AccountInfo();
        accountInfo.setId(1L);
        accountInfo.setFirstName("testFirstName");
        accountInfo.setLastName("testLastName");
        accountInfo.setCountry("testCountry");
        accountInfo.setMonthlyIncome(2400.00);
    }

    // =========== GET /api/v1/accountinfo ===========

    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    @DisplayName("GET accountinfo - success")
    void getAccountInfo() throws Exception {
        // Arrange
        when(accountService.getAccountInfo(any())).thenReturn(new AccountInfoDTO(accountInfo));

        // Act & Assert
        mockMvc.perform(get("/api/v1/accountinfo")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].first_name").value("testFirstName"));

        verify(accountService, times(1)).getAccountInfo(any());
    }

    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    @DisplayName("GET accountinfo - empty")
    void getAccountInfoEmpty() throws Exception {
        // Arrange
        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();

        when(accountService.getAccountInfo(any())).thenReturn(accountInfoDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/accountinfo")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk());

        verify(accountService, times(1)).getAccountInfo(any());
    }

    @Test
    @DisplayName("Get accountindo - unauthorized")
    void getAccountInfoUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/accountinfo"))
                .andExpect(status().isUnauthorized());
    }

    // =========== POST /api/v1/accountinfo ===========

    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    @DisplayName("POST accountinfo - success")
    void postAccountInfoSuccess() throws Exception {
        // Arrange
        doNothing().when(accountService).upsertAccountInfo(any(AccountInfo.class), any());

        // Act & Assert
        mockMvc.perform(post("/api/v1/accountinfo")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content(objectMapper.writeValueAsString(accountInfo))
                .with(csrf()))
                .andExpect(status().isOk());

        verify(accountService, times(1)).upsertAccountInfo(any(AccountInfo.class), any());
    }

    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    @DisplayName("POST accountinfo - empty")
    void postAccountInfoEmpty() throws Exception {
        // Arrange
        AccountInfo postAccountInfo = new AccountInfo();

        doNothing().when(accountService).upsertAccountInfo(any(AccountInfo.class), any());

        // Act & Assert
        mockMvc.perform(post("/api/v1/accountinfo")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content(objectMapper.writeValueAsString(postAccountInfo))
                .with(csrf()))
                .andExpect(status().isOk());

        verify(accountService, times(1)).upsertAccountInfo(any(AccountInfo.class), any());
    }

    @Test
    @DisplayName("POST accountinfo - unauthorized")
    void postAccountInfoUnauthorized() throws Exception {
        // Arrange
        doNothing().when(accountService).upsertAccountInfo(any(AccountInfo.class), any());

        // Act & Assert
        mockMvc.perform(post("/api/v1/accountinfo")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content(objectMapper.writeValueAsString(accountInfo))
                .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(accountService, never()).upsertAccountInfo(any(AccountInfo.class), any());
    }

    // =========== PUT /api/v1/accountinfo ===========

    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    @DisplayName("PUT accountinfo - success")
    void putAccountInfoSuccess() throws Exception {
        // Arrange
        doNothing().when(accountService).upsertAccountInfo(any(AccountInfo.class), any());

        // Act & Assert
        mockMvc.perform(put("/api/v1/accountinfo")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content(objectMapper.writeValueAsString(accountInfo))
                .with(csrf()))
                .andExpect(status().isOk());

        verify(accountService, times(1)).upsertAccountInfo(any(AccountInfo.class), any());
    }

    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    @DisplayName("PUT accountinfo - not found")
    void putAccountInfoNotFound() throws Exception {
        // Arrange
        doThrow(ExpenseNotFoundException.class).when(accountService).upsertAccountInfo(any(AccountInfo.class), any());

        // Act & Assert
        mockMvc.perform(put("/api/v1/accountinfo")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content(objectMapper.writeValueAsString(accountInfo))
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(accountService, times(1)).upsertAccountInfo(any(AccountInfo.class), any());
    }

    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    @DisplayName("PUT accountinfo - empty")
    void putAccountInfoEmpty() throws Exception {
        // Arrange
        AccountInfo putAccountInfo = new AccountInfo();

        doNothing().when(accountService).upsertAccountInfo(any(AccountInfo.class), any());

        // Act & Assert
        mockMvc.perform(put("/api/v1/accountinfo")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content(objectMapper.writeValueAsString(putAccountInfo))
                .with(csrf()))
                .andExpect(status().isOk());

        verify(accountService, times(1)).upsertAccountInfo(any(AccountInfo.class), any());
    }

    @Test
    @DisplayName("PUT accountinfo - unauthorized")
    void putAccountInfoUnauthorized() throws Exception {
        // Arrange
        doNothing().when(accountService).upsertAccountInfo(any(AccountInfo.class), any());

        // Act & Assert
        mockMvc.perform(put("/api/v1/accountinfo")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content(objectMapper.writeValueAsString(accountInfo))
                .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(accountService, never()).upsertAccountInfo(any(AccountInfo.class), any());
    }

    // =========== DELETE /api/v1/accountinfo ===========

    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    @DisplayName("DELETE accountinfo - success")
    void deleteAccountInfoSuccess() throws Exception {
        // Arrange
        doNothing().when(accountService).deleteAccountInfo(any());

        // Act & Assert
        mockMvc.perform(delete("/api/v1/accountinfo")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content(objectMapper.writeValueAsString(accountInfo))
                .with(csrf()))
                .andExpect(status().isOk());

        verify(accountService, times(1)).deleteAccountInfo(any());
    }

    @Test
    @WithMockUser(username = "testUser", authorities = "ROLE_USER")
    @DisplayName("DELETE accountinfo - not found")
    void deleteAccountInfoNotFound() throws Exception {
        // Arrange
        doThrow(ExpenseNotFoundException.class).when(accountService).deleteAccountInfo(any());

        // Act & Assert
        mockMvc.perform(delete("/api/v1/accountinfo")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content(objectMapper.writeValueAsString(accountInfo))
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(accountService, times(1)).deleteAccountInfo(any());
    }

    @Test
    @DisplayName("DELETE accountinfo - unauthorized")
    void deleteAccountInfoUnauthorized() throws Exception {
        // Arrange
        doNothing().when(accountService).deleteAccountInfo(any());

        // Act & Assert
        mockMvc.perform(delete("/api/v1/accountinfo")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content(objectMapper.writeValueAsString(accountInfo))
                .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(accountService, never()).deleteAccountInfo(any());
    }
}