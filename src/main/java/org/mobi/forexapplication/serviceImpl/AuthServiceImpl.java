package org.mobi.forexapplication.serviceImpl;

import jakarta.transaction.Transactional;
import org.mobi.forexapplication.dto.AuthResponse;
import org.mobi.forexapplication.dto.LoginDTO;
import org.mobi.forexapplication.model.PasswordResetOTP;
import org.mobi.forexapplication.model.User;
import org.mobi.forexapplication.repository.PasswordResetOtpRepository;
import org.mobi.forexapplication.repository.UserRepository;
import org.mobi.forexapplication.service.AuthService;
import org.mobi.forexapplication.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordResetOtpRepository passwordResetOtpRepository;


    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);


    @Override
    public User register(User user) {
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
        // No logic needed for stateless JWT
        throw new UnsupportedOperationException("Logout is handled client-side by clearing the JWT token.");
    }

    @Override
    public Optional<User> getUserByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public void sendPasswordResetOTP(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Email not found");
        }

        User user = userOpt.get();

        // Optional cleanup of previous OTP
        passwordResetOtpRepository.deleteByUser(user);
        passwordResetOtpRepository.flush();

        // Generate a 6-digit OTP
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);

        PasswordResetOTP resetOTP = new PasswordResetOTP(otp, user, expiry);
        passwordResetOtpRepository.save(resetOTP);

        String messageBody = "Hello " + user.getUsername() + ",\n\n" +
                "Your password reset OTP is: " + otp + "\n" +
                "It will expire in 15 minutes.\n\n" +
                "If you did not request this, please ignore.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset OTP");
        message.setText(messageBody);

        mailSender.send(message);
        logger.info("OTP sent to {}", email);
    }

    @Override
    @Transactional
    public void resetPasswordWithOTP(String email, String otp, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email"));

        PasswordResetOTP resetOTP = passwordResetOtpRepository.findByUserAndOtp(user, otp)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        if (resetOTP.isUsed()) {
            throw new RuntimeException("OTP has already been used.");
        }

        if (resetOTP.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired.");
        }

        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);

        resetOTP.setUsed(true);
        passwordResetOtpRepository.save(resetOTP);

        logger.info("Password reset successfully for user: {}", email);
    }
}
