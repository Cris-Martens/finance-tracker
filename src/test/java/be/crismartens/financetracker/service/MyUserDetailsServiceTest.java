package be.crismartens.financetracker.service;

import be.crismartens.financetracker.InvalidUserException;
import be.crismartens.financetracker.model.AppUser;
import be.crismartens.financetracker.repository.UserRepository;
import be.crismartens.financetracker.response.UserResponse;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class MyUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidationService userValidationService;

    @InjectMocks
    private MyUserDetailsService userDetailsService;

    static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private AppUser appUser;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        // Set up test user
        appUser = new AppUser();
        appUser.setUsername("testUser");
        appUser.setPassword("ValidPass123!");
        appUser.setEmail("user@example.com");
        appUser.setAuthority("ROLE_USER");

        // Set up user details;
        userDetails = new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of();
            }

            @Override
            public @Nullable String getPassword() {
                return "ValidPass123!";
            }

            @Override
            public String getUsername() {
                return "testUser";
            }
        };
    }

    // ============ Test registration methods ============
    @Test
    void registerUser_WhenUserExists_ThrowsException() {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.ofNullable(appUser));

        // Act
        ResponseEntity<UserResponse> result = userDetailsService.registerUser(appUser);

        // Assert
        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(userRepository, never()).save(any());

    }

    @Test
    void registerUser_InvalidEmail_ThrowsException() {
        // Arrange
        AppUser newUser = new AppUser();
        newUser.setUsername("testUser");
        newUser.setEmail("InvalidEmail");
        newUser.setPassword("ValidPass123!");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidUserException.class, () -> userDetailsService.registerUser(appUser));
    }

    @Test
    void registerUser_WeakPassword_ThrowsException() {
        // Arrange
        AppUser newUser = new AppUser();
        newUser.setUsername("testUser");
        newUser.setEmail("test@example.com");
        newUser.setPassword("weakPassword");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidUserException.class, () -> userDetailsService.registerUser(newUser));
    }

    @Test
    void registerUser_AddNewUser() {
        // Arrange
        AppUser newUser = new AppUser();
        newUser.setUsername("testUser");
        newUser.setEmail("test@example.com");
        newUser.setPassword("ValidPass123!");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        when(userValidationService.isValid(newUser)).thenReturn(true);

        // Act
        ResponseEntity<UserResponse> response = userDetailsService.registerUser(newUser);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("New User saved: ", response.getBody().getMessage());
        assertEquals("testUser",  response.getBody().getUsername());
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(userRepository, times(1)).save(any(AppUser.class));
    }

    // ============ deleteUser() Tests ============

    @Test
    void deleteUser_UserDoesNotExist_ThrowsException() {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        // Act
        userDetailsService.deleteUser(userDetails);

        // Act & Assert
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(userRepository, never()).delete(any());
    }

    @Test
    void deleteUser_UserDoesExist() {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(appUser));

        // Act
        userDetailsService.deleteUser(userDetails);

        // Assert
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(userRepository, times(1)).delete(any(AppUser.class));
    }

    @Test
    void deleteUser_NoUserGiver_ThrowsException() {
        // Arrange
        UserDetails newUserDetails = mock(UserDetails.class);

        when(userRepository.findByUsername(null)).thenReturn(Optional.empty());

        // Act
        userDetailsService.deleteUser(newUserDetails);

        // Assert
        verify(userRepository, times(1)).findByUsername(null);
        verify(userRepository, never()).delete(any());
    }

    // ============ loadByUsername() Tests ============

    @Test
    void loadByUsername_UserDoesNotExist_ThrowsException() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("nonexistent"));
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void loadByUsername_UserExists() {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(appUser));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("testUser");

        // Assert
        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        assertEquals("ValidPass123!", result.getPassword());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        verify(userRepository, times(1)).findByUsername("testUser");
    }
}