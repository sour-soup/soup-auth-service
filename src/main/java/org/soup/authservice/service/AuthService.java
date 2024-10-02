package org.soup.authservice.service;

import org.soup.authservice.model.User;

public interface AuthService {
    void registerUser(String username, String password);

    User authenticateUser(String username, String password);
}
