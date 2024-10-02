package org.soup.authservice.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.soup.authservice.model.User;

public interface TokenService {
    void addAuthCookiesToResponse(User user, HttpServletResponse response);

    void refreshTokens(HttpServletRequest request, HttpServletResponse response);
}
