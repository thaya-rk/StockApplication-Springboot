package org.mobi.forexapplication.serviceImpl;

import jakarta.transaction.Transactional;
import org.mobi.forexapplication.dto.AuthResponse;
import org.mobi.forexapplication.dto.LoginDTO;
import org.mobi.forexapplication.model.PasswordResetOTP;
import org.mobi.forexapplication.model.User;
import org.mobi.forexapplication.service.AuthService;
import org.mobi.forexapplication.security.JwtUtil;
import org.mobi.forexapplication.repository.UserRepository;
import org.mobi.forexapplication.utils.OTPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private OTPService otpService;

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Override
    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }


    @Override
    public AuthResponse login(LoginDTO loginDTO) {
        Optional<User> userOpt = userRepository.findByUsername(loginDTO.getUsernameOrEmail());
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(loginDTO.getUsernameOrEmail());
        }

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (encoder.matches(loginDTO.getPassword(), user.getPassword())) {
                String token = jwtUtil.generateToken(
                        user.getUsername(),
                        user.getUserId(),
                        jwtUtil.getAuthoritiesFromRole(user.getRole())
                );
                return new AuthResponse("Login successful", token, user.getUsername());
            }
        }
        throw new RuntimeException("Invalid credentials");
    }

    @Override
    public void logout() {
        throw new UnsupportedOperationException("Logout is handled client-side by clearing the JWT token.");
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public void sendPasswordResetOTP(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        otpService.clearOtpForUser(user);
        var otpEntity = otpService.createAndSaveOtp(user, "RESET_PASSWORD");
        otpService.sendOtpEmail(user, "Password Reset OTP",
                "Hello " + user.getUsername() + ",\n\nYour password reset OTP is: ", otpEntity.getOtp());

        logger.info("OTP sent to {}", email);
    }

    @Override
    @Transactional
    public void resetPasswordWithOTP(String email, String otp, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email"));

        otpService.validateOtp(user, otp, "RESET_PASSWORD");

        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);

        logger.info("Password reset successfully for user: {}", email);
    }


    @Override
    @Transactional
    public void sendEmailVerificationOtp(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        otpService.clearOtpForUser(user);
        PasswordResetOTP otpEntity = otpService.createAndSaveOtp(user, "EMAIL_VERIFICATION");

        otpService.sendOtpEmail(user, "Email Verification OTP",
                "Hello " + user.getUsername() + ",\n\nYour email verification OTP is: ",
                otpEntity.getOtp());
    }

    @Override
    @Transactional
    public void verifyEmailOtp(String username, String otp) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        otpService.validateOtp(user, otp, "EMAIL_VERIFICATION");

        user.setEmailVerified(true); // Add this field to your User entity if not already
        userRepository.save(user);

        logger.info("Email verified successfully for user: {}", username);
    }

}
