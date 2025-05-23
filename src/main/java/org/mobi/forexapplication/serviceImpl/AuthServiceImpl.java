package org.mobi.forexapplication.serviceImpl;

import org.mobi.forexapplication.dto.AuthResponse;
import org.mobi.forexapplication.dto.LoginDTO;
import org.mobi.forexapplication.model.PasswordResetToken;
import org.mobi.forexapplication.model.User;
import org.mobi.forexapplication.repository.PasswordResetTokenRepository;
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
    private PasswordResetTokenRepository passwordResetTokenRepository;

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

    public void sendPasswordResetToken(String email) {
        logger.info("Starting sendPasswordResetToken for email: {}", email);
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            logger.error("Email not found: {}", email);
            throw new RuntimeException("Email not found");
        }
        User user = userOpt.get();

        String token = UUID.randomUUID().toString();
        logger.info("Generated token: {}", token);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        passwordResetTokenRepository.save(resetToken);

        String resetLink = "http://localhost:4200/reset-password?token=" + token;
        logger.info("Reset link: {}", resetLink);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Password Reset Request");
            message.setText("Hello " + user.getUsername() + ",\n\n" +
                    "We received a request to reset your password. Use the link below to reset it:\n" +
                    resetLink + "\n\n" +
                    "This link will expire in 15 minutes.");

            System.out.println("Sending email to: " + message.getTo()[0]);
            System.out.println("Subject: " + message.getSubject());
            System.out.println("Body: " + message.getText());
            mailSender.send(message);
            logger.info("Password reset email sent successfully to {}", email);
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", email, e.getMessage());
            throw new RuntimeException("Failed to send email");
        }
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = resetToken.getUser();
        System.out.println("User from reset token is "+user.getUsername());
        logger.info("Encoded new password: {}", encoder.encode(newPassword));
        user.setPassword(encoder.encode(newPassword));

        userRepository.save(user);
        logger.info("User password updated for email: {}", user.getEmail());
        passwordResetTokenRepository.delete(resetToken);
    }

}
