package org.mobi.forexapplication.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.mobi.forexapplication.Exception.GlobalCustomException;
import org.mobi.forexapplication.dto.ApiResponse;
import org.mobi.forexapplication.dto.AuthResponse;
import org.mobi.forexapplication.dto.LoginDTO;
import org.mobi.forexapplication.dto.UserDTO;
import org.mobi.forexapplication.model.User;
import org.mobi.forexapplication.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody User user) {
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            throw new GlobalCustomException("Username and password are required.");
        }
        try {
            User savedUser = authService.register(user);
            return ResponseEntity.ok(new ApiResponse<>(
                    "User registered successfully",
                    new AuthResponse("User registered", null, savedUser.getUsername())
            ));
        } catch (Exception e) {
            throw new GlobalCustomException("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        if (loginDTO.getUsernameOrEmail() == null || loginDTO.getPassword() == null) {
            throw new GlobalCustomException("Username and password are required.");
        }

        try {
            AuthResponse authResponse = authService.login(loginDTO);
            Cookie jwtCookie = new Cookie("token", authResponse.getToken());
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(24 * 60 * 60);
            response.addCookie(jwtCookie);

            return ResponseEntity.ok(new ApiResponse<>("Login successful", authResponse));
        } catch (Exception e) {
            throw new GlobalCustomException("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("token", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);
        return ResponseEntity.ok(new ApiResponse<>("Logged out successfully", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

            return authService.getUserByUsername(username)
                    .map(user -> ResponseEntity.ok(new ApiResponse<>(
                            "User info",
                            new UserDTO(user.getUsername(), user.getEmail(), user.getRole(), user.isEmailVerified())
                    )))
                    .orElseThrow(() -> new GlobalCustomException("User not found"));
        }

        throw new GlobalCustomException("No user logged in.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> sendOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            throw new GlobalCustomException("Email is required.");
        }

        authService.sendPasswordResetOTP(email);
        return ResponseEntity.ok(new ApiResponse<>("OTP sent to your email", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String otp = body.get("otp");
        String newPassword = body.get("newPassword");

        if (email == null || otp == null || newPassword == null) {
            throw new GlobalCustomException("All fields (email, OTP, newPassword) are required.");
        }

        authService.resetPasswordWithOTP(email, otp, newPassword);
        return ResponseEntity.ok(new ApiResponse<>("Password reset successful", null));
    }

    @PostMapping("/send-verification-email")
    public ResponseEntity<ApiResponse<Boolean>> sendEmailVerification(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new GlobalCustomException("User not authenticated.");
        }

        authService.sendEmailVerificationOtp(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>("Verification email sent.", true));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Boolean>> verifyEmail(@RequestBody Map<String, String> requestBody, Principal principal) {
        String otp = requestBody.get("otp");
        if (principal == null || principal.getName() == null) {
            throw new GlobalCustomException("User not authenticated.");
        }

        if (otp == null) {
            throw new GlobalCustomException("OTP is required.");
        }

        authService.verifyEmailOtp(principal.getName(), otp);
        return ResponseEntity.ok(new ApiResponse<>("Email verified successfully.", true));
    }
}
