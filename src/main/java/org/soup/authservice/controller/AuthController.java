package org.soup.authservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.soup.authservice.model.User;
import org.soup.authservice.service.AuthService;
import org.soup.authservice.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final AuthService authService;

    private final TokenService tokenService;

    public AuthController(AuthService authService, TokenService tokenService) {
        this.authService = authService;
        this.tokenService = tokenService;
    }

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(
            @RequestParam("user") String username,
            @RequestParam("password") String password) {
        authService.registerUser(username, password);
    }

    @PostMapping("/auth")
    @ResponseStatus(HttpStatus.OK)
    public void auth(
            @RequestParam("user") String username,
            @RequestParam("password") String password,
            HttpServletResponse response) {
        User user = authService.authenticateUser(username, password);
        tokenService.addAuthCookiesToResponse(user, response);
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        tokenService.refreshTokens(request, response);
    }
}
