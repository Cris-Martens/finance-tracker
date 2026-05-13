package be.crismartens.financetracker.integrationtests;

import be.crismartens.financetracker.model.AppUser;
import be.crismartens.financetracker.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@Transactional
public class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Register a new user successfully")
    void testRegisterUserSucces() throws Exception {
        // Arrange
        AppUser user = new AppUser();
        user.setUsername("example@test.com");
        user.setPassword("ValidPass123!");

        // Act
       mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("example@test.com")))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andReturn();

        // Assert
        assertThat(userRepository.findByUsername("example@test.com")).isPresent();

        // Assert
        AppUser savedUser = userRepository.findByUsername("example@test.com").get();
        assertThat(passwordEncoder.matches("ValidPass123!", savedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("Should fail to register user with duplicate username")
    void testRegisterDuplicateUserFail() throws Exception {
        // Arrange
        AppUser user = new AppUser();
        user.setUsername("duplicate@example.com");
        user.setPassword("ValidPass123!");

        // add user to the database a first time
        mockMvc.perform(post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                        .andExpect(status().isOk());
        assertThat(userRepository.findByUsername("duplicate@test.com")).isPresent();

        // Try to add user with duplicate username
        AppUser duplicateUser = new AppUser();
        duplicateUser.setUsername("duplicate@test.com");
        duplicateUser.setPassword("AnotherPass123!");

        mockMvc.perform(post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateUser)))
                .andExpect(status().isConflict());

        // Assert
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should fail to register user with duplicate email")
    void testRegisterDuplicateEmailFail() throws Exception {
        // Arrange
        AppUser user = new AppUser();
        user.setUsername("duplicates@test.com");
        user.setPassword("ValidPass123!");

        mockMvc.perform(post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        // Act - add second user with duplicate email
        AppUser duplicateUser = new AppUser();
        duplicateUser.setUsername("duplicates@test.com");
        duplicateUser.setPassword("AnotherPass123!");

        mockMvc.perform(post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateUser)))
                .andExpect(status().isConflict());

        // Assert
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should delete authenticated user")
    @WithMockUser(username = "deleteUser", authorities = {"ROLE_USER"})
    void testDeleteUserSuccess() throws Exception {
        // Arrange
        AppUser userToDelete = new AppUser();
        userToDelete.setUsername("delete@test.com");
        userToDelete.setPassword(passwordEncoder.encode("ValidPass123!"));
        userRepository.save(userToDelete);

        assertThat(userRepository.findByUsername("delete@test.com")).isPresent();

        // Act
        mockMvc.perform(delete("/api/v1/user/delete"))
                .andExpect(status().isOk());

        // Assert
        assertThat(userRepository.findByUsername("delete@test.com")).isEmpty();
    }

    @Test
    @DisplayName("Should require authentication to delete user")
    void testDeleteUserFail() throws Exception {
        mockMvc.perform(delete("/api/v1/user/delete"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should handle invalid JSON gracefully")
    void testRegisterInvalidJSON() throws Exception {
        // Arrange
        String jsonString = """
                {
                    invalid json
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonString))
                .andExpect(status().isBadRequest());
        assertThat(userRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should reject registration with empty username")
    void testRegisterEmptyUsername() throws Exception {
        // Arrange
        AppUser user = new AppUser();
        user.setUsername("");
        user.setPassword("ValidPass123!");

        // Act
        mockMvc.perform(post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());

        // Assert
        assertThat(userRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should reject registration with emtpy password")
    void testRegisterEmptyPassword() throws Exception {
        // Arrange
        String userJson = """
                {
                    "username": "nullpass@test.com"
                    "password": null
                }
                """;

        // Act
        mockMvc.perform(post("/api/v1/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(userJson))
                .andExpect(status().isBadRequest());

        // Assert
        assertThat(userRepository.findByUsername("nullpassUser")).isEmpty();
    }


    @Test
    @DisplayName("Should handle concurrent registration attempts")
    void testRegisterUser_ConcurrentRequests() throws Exception {
        // Given
        AppUser user = new AppUser();
        user.setUsername("concurrent@test.com");
        user.setPassword("ValidPass123!");

        String userJson = objectMapper.writeValueAsString(user);

        // When - simulate concurrent requests
        int threadCount = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    var result = mockMvc.perform(post("/api/v1/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
                            .andReturn();

                    if (result.getResponse().getStatus() == 200) {
                        successCount.incrementAndGet();
                    } else {
                        failureCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        // Wait for all threads to complete
        latch.await(10, TimeUnit.SECONDS);

        // Then - Only one should succeed
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failureCount.get()).isEqualTo(threadCount - 1);
        assertThat(userRepository.findByUsername("concurrent@test.com")).isPresent();
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should validate password strength requirements")
    void testRegisterUser_WeakPassword() throws Exception {
        // Arrange
        String weakPassJson = """
                {
                    "username": "weakpass@test.com",
                    "password": "weak",
                }
                """;

        // Act
        mockMvc.perform(post("/api/v1/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(weakPassJson))
                .andExpect(status().isBadRequest());

        // Assert
        assertThat(userRepository.findByUsername("weakpass@test.com")).isEmpty();
    }

    @Test
    @DisplayName("Should handle SQL injection")
    void testRegisterUser_SQLInjection() throws Exception {
        // Given - Username with SQL injection attempt
        String maliciousJson = """
                {
                    "username": "admin'; DROP TABLE app_users; --",
                    "password": "ValidPass123!",
                }
                """;

        // Act
        mockMvc.perform(post("/api/v1/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(maliciousJson))
                .andExpect(status().isOk());

        // Assert - User was created, table not dropped
        assertThat(userRepository.findByUsername("admin'; DROP TABLE user; --")).isPresent();
        assertThat(userRepository.count()).isGreaterThan(0);


    }

    @Test
    @DisplayName("Should handle very long usernames appropriately")
    void testRegisterUser_VeryLongUsernames() throws Exception {
        // Arrange
        String longUsername = "a".repeat(248) + "@test.com";
        AppUser user = new AppUser();
        user.setUsername(longUsername);
        user.setPassword("ValidPass123!");

        // Act
        mockMvc.perform(post("/api/v1/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should successfully delete user and casecade related date")
    @WithMockUser(username = "cascadeUser", authorities = {"ROLE_USER"})
    @Transactional
    void testDeleteUser_CascadeDelete() throws Exception {
        AppUser user = new AppUser();
        user.setUsername("cascade@test.com");
        user.setPassword("ValidPass123!");
        userRepository.save(user);

        // Add related repositories to check properly

        assertThat(userRepository.findByUsername("cascadeUser")).isPresent();

        // Act
        mockMvc.perform(delete("/api/v1/delete"))
                .andExpect(status().isOk());

        // Assert
        assertThat(userRepository.findByUsername("cascadeUser")).isEmpty();
    }

    @Test
    @DisplayName("Should trim whitespace from username")
    void testRegisterUser_TrimWhitespaceFromUsername() throws Exception {
        // Arrange
        AppUser user = new AppUser();
        user.setUsername("trim@test.com      ");
        user.setPassword("ValidPass123!");

        // Act
        mockMvc.perform(post("/api/v1/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("trim@test.com"));
    }
}
