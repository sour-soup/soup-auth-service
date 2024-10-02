package org.soup.authservice.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.soup.authservice.model.User;
import org.soup.authservice.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {
    private static final String ACCESS_TOKEN_NAME = "Soup-Access-Token";
    private static final String REFRESH_TOKEN_NAME = "Soup-Refresh-Token";

    private final JwtUtils jwtUtils;

    @Value("${security.access-token.expirationMillis}")
    private Long accessTokenExpirationMillis;

    @Value("${security.refresh-token.expirationMillis}")
    private Long refreshTokenExpirationMillis;

    public TokenServiceImpl(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void addAuthCookiesToResponse(User user, HttpServletResponse response) {
        String accessToken = jwtUtils.generateToken(user, accessTokenExpirationMillis);
        String refreshToken = jwtUtils.generateToken(user, refreshTokenExpirationMillis);

        addCookie(response, ACCESS_TOKEN_NAME, accessToken, null, accessTokenExpirationMillis);
        addCookie(response, REFRESH_TOKEN_NAME, refreshToken, "/refresh", 2592000000L);
    }

    @Override
    public void refreshTokens(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getCookieValue(request, REFRESH_TOKEN_NAME)
                .orElseThrow(() -> new InvalidCookieException("Refresh token not found"));

        if (!jwtUtils.verifyToken(refreshToken)) {
            throw new InvalidCookieException("Refresh token invalid");
        }

        UUID id = jwtUtils.getIdFromToken(refreshToken);
        String username = jwtUtils.getUsernameFromToken(refreshToken);
        User user = new User(id, username);
        addAuthCookiesToResponse(user, response);
    }

    private Optional<String> getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> name.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    private void addCookie(HttpServletResponse response, String name, String value, String path, long expirationMillis) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) expirationMillis / 1000);
        if (path != null) {
            cookie.setPath(path);
        }

        response.addCookie(cookie);
    }
}
