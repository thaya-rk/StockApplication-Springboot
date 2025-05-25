package org.mobi.forexapplication.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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

import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody User user) {
        try {

            User savedUser = authService.register(user);
            return ResponseEntity.ok(new ApiResponse<>(
                    "User registered successfully",
                    new AuthResponse("User registered", null, savedUser.getUsername())
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    "Registration failed: " + e.getMessage(),
                    new AuthResponse("Failed", null, null)
            ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        try {
            AuthResponse authResponse = authService.login(loginDTO);
            Cookie jwtCookie = new Cookie("token", authResponse.getToken());
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(24 * 60 * 60);

            response.addCookie(jwtCookie);

            return ResponseEntity.ok(new ApiResponse<>("Login successful", authResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse<>("Login failed: " + e.getMessage(), null)
            );
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
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();

                return authService.getUserByUsername(username)
                        .map(user -> ResponseEntity.ok(new ApiResponse<>
                                ("User info",
                                        new UserDTO(user.getUsername(), user.getEmail(),user.getRole()))))
                        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ApiResponse<>("User not found", null)));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>("No user logged in", null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to retrieve user: " + e.getMessage(), null));
        }

    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> sendOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        try {
            authService.sendPasswordResetOTP(email);
            return ResponseEntity.ok(new ApiResponse<>("OTP sent to your email", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("Failed: " + e.getMessage(), null));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String otp = body.get("otp");
        String newPassword = body.get("newPassword");

        try {
            authService.resetPasswordWithOTP(email, otp, newPassword);
            return ResponseEntity.ok(new ApiResponse<>("Password reset successful", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("Failed: " + e.getMessage(), null));
        }
    }



}
