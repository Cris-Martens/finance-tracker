package be.crismartens.financetracker.unittesting.controller;

import be.crismartens.financetracker.exceptions.InvalidUserException;
import be.crismartens.financetracker.exceptions.UsernameAlreadyInUseExcepion;
import be.crismartens.financetracker.auth.MyUserDetailsService;
import be.crismartens.financetracker.dto.AppUserDTO;
import be.crismartens.financetracker.model.AppUser;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("User Controller Test")
class UserControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private MyUserDetailsService userDetailsService;

    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    AppUser user;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        objectMapper = new ObjectMapper();

        user = new AppUser();
        user.setUsername("test@example.com");
        user.setPassword("ValidPass123!");
    }

    // ======= Register User Tests =======

    @Test
    @DisplayName("Register User Test - Success")
    void registerUser() throws Exception {
        // Arrange
        when(userDetailsService.registerUser(any())).thenReturn(new AppUserDTO(user));

        // Act & Assert
        mockMvc.perform(post("/api/v1/register")
        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("test@example.com"));

        verify(userDetailsService, times(1)).registerUser(any());
    }

    @Test
    @DisplayName("Register User Test - Conflicting username")
    void registerUserConflictingUsername() throws Exception {
        // Arrange
        doThrow(UsernameAlreadyInUseExcepion.class)
                .when(userDetailsService).registerUser(any());

        AppUser duplicateUser = new AppUser();
        duplicateUser.setUsername("duplicate@example.com");
        duplicateUser.setPassword("ValidPass123!");

        // Act & Assert
        mockMvc.perform(post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateUser))
                .with(csrf()))
                .andExpect(status().isConflict());

        verify(userDetailsService, times(1)).registerUser(any());
    }

    @Test
    @DisplayName("Register User Test - Invalid username and/or password")
    void registerUserBadRequest() throws Exception {
        // Arrange
        AppUser invalidUser = new AppUser();
        invalidUser.setUsername("invalid");
        invalidUser.setPassword("weak!");

        doThrow(InvalidUserException.class).when(userDetailsService).registerUser(any());

        // Act & Assert
        mockMvc.perform(post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser))
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(userDetailsService, times(1)).registerUser(any());
    }

    // ======= Delete User Tests =======

    @Test
    @WithMockUser
    @DisplayName("Delete User - Success")
    void deleteUser() throws Exception {
        // Arrange
        doNothing().when(userDetailsService).deleteUser(any());

        // Act & Assert
        mockMvc.perform(delete("/api/v1/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(userDetailsService, times(1)).deleteUser(any());
    }

    @Test
    @DisplayName("Delete User - Unauthorized")
    void deleteUserUnauthorized() throws Exception {
        // Act & Arrange
        mockMvc.perform(delete("/api/v1/delete")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(userDetailsService, never()).deleteUser(any());
    }
}