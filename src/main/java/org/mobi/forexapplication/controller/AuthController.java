package org.mobi.forexapplication.controller;

import jakarta.servlet.http.HttpSession;
import org.mobi.forexapplication.dto.ApiResponse;
import org.mobi.forexapplication.dto.AuthResponse;
import org.mobi.forexapplication.dto.LoginDTO;
import org.mobi.forexapplication.dto.UserDTO;
import org.mobi.forexapplication.model.User;
import org.mobi.forexapplication.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // Register endpoint
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody User user) {
        try {
            User savedUser = authService.register(user);
            ApiResponse<AuthResponse> response = new ApiResponse<>(
                    "User registered successfully",
                    new AuthResponse("User registered successfully", savedUser.getUsername())
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<AuthResponse> response = new ApiResponse<>(
                    "Registration failed: " + e.getMessage(),
                    new AuthResponse("Registration failed", null)
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Login endpoint
    @PostMapping("/login")

    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginDTO loginDTO, HttpSession session) {
        try {
            User user = authService.login(loginDTO.getUsernameOrEmail(), loginDTO.getPassword());
            // Set user in session
            session.setAttribute("user", user);

            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .authorities(user.getRole()) // or user.getAuthority() if you store roles like "ROLE_ADMIN"
                    .build();

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);


            ApiResponse<AuthResponse> response = new ApiResponse<>(
                    "Login successful",
                    new AuthResponse("Login successful", user.getUsername())
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<AuthResponse> response = new ApiResponse<>(
                    "Login failed: " + e.getMessage(),
                    new AuthResponse("Login failed", null)
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // Logout endpoint
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpSession session) {
        try{
            authService.logout(session);
            
            return ResponseEntity.ok(new ApiResponse<>("Logged out successfully", null));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(
                    e.getMessage(), null));
        }
    }

    // Get current user endpoint
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()
                    && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {

                org.springframework.security.core.userdetails.User userDetails =
                        (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

                UserDTO userDTO = new UserDTO(userDetails.getUsername(), null); // You can add logic to fetch email if needed

                ApiResponse<UserDTO> response = new ApiResponse<>(
                        "Current user retrieved successfully",
                        userDTO
                );
                return ResponseEntity.ok(response);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse<>("No user logged in", null)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>("Failed to retrieve user: " + e.getMessage(), null)
            );
        }
    }


}
