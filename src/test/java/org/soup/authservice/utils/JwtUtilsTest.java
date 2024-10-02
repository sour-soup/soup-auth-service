package org.soup.authservice.utils;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.soup.authservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
class JwtUtilsTest {
    @Autowired
    private JwtUtils jwtUtils;

    @Test
    @DisplayName("Should generate a valid token")
    void generateToken_shouldGenerateValidToken() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "user");
        long expirationMillis = 60000;

        // Act
        String token = jwtUtils.generateToken(user, expirationMillis);

        // Assert
        assertThat(token).isNotEmpty();
    }

    @Test
    @DisplayName("Should verify valid token")
    void verifyToken_shouldReturnTrueForValidToken() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "user");
        String token = jwtUtils.generateToken(user, 60000);

        // Act
        boolean isValid = jwtUtils.verifyToken(token);

        // Assert
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should return false for invalid token")
    void verifyToken_shouldReturnFalseForInvalidToken() {
        // Act
        boolean isValid = jwtUtils.verifyToken("invalid-token");

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should extract username from token")
    void getUsernameFromToken_shouldReturnCorrectUsername() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "user");
        String token = jwtUtils.generateToken(user, 60000);

        // Act
        String username = jwtUtils.getUsernameFromToken(token);

        // Assert
        assertThat(username).isEqualTo("user");
    }

    @Test
    @DisplayName("Should throw exception for invalid token when getting username")
    void getUsernameFromToken_shouldThrowExceptionForInvalidToken() {
        // Act & Assert
        assertThatThrownBy(() -> jwtUtils.getUsernameFromToken("invalid-token"))
                .isInstanceOf(JWTVerificationException.class);
    }

    @Test
    @DisplayName("Should extract user ID from token")
    void getIdFromToken_shouldReturnCorrectId() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "user");
        String token = jwtUtils.generateToken(user, 60000);

        // Act
        UUID extractedId = jwtUtils.getIdFromToken(token);

        // Assert
        assertThat(extractedId).isEqualTo(userId);
    }

    @Test
    @DisplayName("Should throw exception for invalid token when getting user ID")
    void getIdFromToken_shouldThrowExceptionForInvalidToken() {
        // Act & Assert
        assertThatThrownBy(() -> jwtUtils.getIdFromToken("invalid-token"))
                .isInstanceOf(JWTVerificationException.class);
    }
}