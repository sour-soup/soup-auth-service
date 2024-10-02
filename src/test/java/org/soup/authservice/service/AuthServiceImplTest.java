package org.soup.authservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.soup.authservice.model.User;
import org.soup.authservice.repository.UserRepository;
import org.soup.authservice.repository.entity.UserEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {
    AutoCloseable openMocks;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should register a new user when username is unique")
    void registerUser_shouldRegisterNewUser_whenUsernameIsUnique() {
        // Arrange
        String username = "user";
        String password = "password";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        // Act
        authService.registerUser(username, password);

        // Assert
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void registerUser_shouldThrowException_whenUsernameAlreadyExists() {
        // Arrange
        String username = "existingUser";
        String password = "password";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new UserEntity()));

        // Act & Assert
        assertThatThrownBy(() -> authService.registerUser(username, password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User already exists");

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should return user when credentials are valid")
    void authenticateUser_shouldReturnUser_whenCredentialsAreValid() {
        // Arrange
        String username = "user";
        String password = "password";
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword("encodedPassword");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(password, userEntity.getPassword())).thenReturn(true);

        // Act
        User user = authService.authenticateUser(username, password);

        // Assert
        assertThat(user.username()).isEqualTo(username);
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("Should throw exception when user is not found")
    void authenticateUser_shouldThrowException_whenUserNotFound() {
        // Arrange
        String username = "nonExistentUser";
        String password = "password";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.authenticateUser(username, password))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    @DisplayName("Should throw exception when password is invalid")
    void authenticateUser_shouldThrowException_whenPasswordIsInvalid() {
        // Arrange
        String username = "user";
        String password = "wrongPassword";
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword("encodedPassword");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(password, userEntity.getPassword())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authService.authenticateUser(username, password))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid username or password");
    }
}