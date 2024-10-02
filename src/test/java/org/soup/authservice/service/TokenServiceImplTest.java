package org.soup.authservice.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.soup.authservice.model.User;
import org.soup.authservice.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
class TokenServiceImplTest {
    AutoCloseable openMocks;

    @MockBean
    private JwtUtils jwtUtils;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Autowired
    @InjectMocks
    private TokenServiceImpl tokenService;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USERNAME = "test";
    private static final String VALID_ACCESS_TOKEN = "valid-access-token";
    private static final String VALID_REFRESH_TOKEN = "valid-refresh-token";

    @Test
    @DisplayName("Should add auth cookies to response")
    void addAuthCookiesToResponse_shouldAddCookiesToResponse() {
        // Arrange
        User user = new User(USER_ID, USERNAME);
        when(jwtUtils.generateToken(user, 900000L)).thenReturn(VALID_ACCESS_TOKEN);
        when(jwtUtils.generateToken(user, 2592000000L)).thenReturn(VALID_REFRESH_TOKEN);

        // Act
        tokenService.addAuthCookiesToResponse(user, response);

        // Assert
        verify(response, times(2)).addCookie(any(Cookie.class));
    }

    @Test
    @DisplayName("Should refresh tokens and add new cookies to response")
    void refreshTokens_shouldRefreshTokensAndAddCookies() {
        // Arrange
        Cookie refreshTokenCookie = new Cookie("Soup-Refresh-Token", VALID_REFRESH_TOKEN);
        when(request.getCookies()).thenReturn(new Cookie[]{refreshTokenCookie});
        when(jwtUtils.verifyToken(VALID_REFRESH_TOKEN)).thenReturn(true);
        when(jwtUtils.getIdFromToken(VALID_REFRESH_TOKEN)).thenReturn(USER_ID);
        when(jwtUtils.getUsernameFromToken(VALID_REFRESH_TOKEN)).thenReturn(USERNAME);

        // Act
        tokenService.refreshTokens(request, response);

        // Assert
        verify(jwtUtils).verifyToken(VALID_REFRESH_TOKEN);
        verify(response, times(2)).addCookie(any(Cookie.class));
    }

    @Test
    @DisplayName("Should throw InvalidCookieException if refresh token is missing")
    void refreshTokens_shouldThrowExceptionIfRefreshTokenNotFound() {
        // Arrange
        when(request.getCookies()).thenReturn(new Cookie[]{});

        // Act & Assert
        assertThatThrownBy(() -> tokenService.refreshTokens(request, response))
                .isInstanceOf(InvalidCookieException.class)
                .hasMessage("Refresh token not found");
    }

    @Test
    @DisplayName("Should throw InvalidCookieException if refresh token is invalid")
    void refreshTokens_shouldThrowExceptionForInvalidRefreshToken() {
        // Arrange
        Cookie refreshTokenCookie = new Cookie("Soup-Refresh-Token", "invalid-token");
        when(request.getCookies()).thenReturn(new Cookie[]{refreshTokenCookie});
        when(jwtUtils.verifyToken(anyString())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> tokenService.refreshTokens(request, response))
                .isInstanceOf(InvalidCookieException.class)
                .hasMessage("Refresh token invalid");
    }
}