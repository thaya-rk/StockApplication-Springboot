package org.mobi.forexapplication.service;

import org.mobi.forexapplication.dto.AuthResponse;
import org.mobi.forexapplication.dto.LoginDTO;
import org.mobi.forexapplication.model.User;

import java.util.Optional;

public interface AuthService {
    User register(User user);

    AuthResponse login(LoginDTO loginDTO);

    void logout();

    Optional<User> getUserByUsername(String username);

    void sendPasswordResetOTP(String email);

    void resetPasswordWithOTP(String email,String otp,String newPassword);

    void sendEmailVerificationOtp(String username);

    void verifyEmailOtp(String username, String otp);

}
