package org.mobi.forexapplication.serviceImpl;

import org.mobi.forexapplication.dto.AuthResponse;
import org.mobi.forexapplication.dto.LoginDTO;
import org.mobi.forexapplication.model.User;
import org.mobi.forexapplication.repository.UserRepository;
import org.mobi.forexapplication.service.AuthService;
import org.mobi.forexapplication.security.JwtUtil;
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
}
