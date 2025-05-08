package org.mobi.forexapplication.service;

import jakarta.servlet.http.HttpSession;
import org.mobi.forexapplication.model.User;

public interface AuthService {
    User register(User user);
    User login(String usernameOrEmail, String password);
    void logout(HttpSession session);
}
