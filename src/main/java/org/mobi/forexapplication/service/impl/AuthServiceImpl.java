package org.mobi.forexapplication.service.impl;

import jakarta.servlet.http.HttpSession;
import org.mobi.forexapplication.model.User;
import org.mobi.forexapplication.repository.UserRepository;
import org.mobi.forexapplication.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public User register(User user) {
        // hash password before saving
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User login(String usernameOrEmail, String password) {
        // Check by username or email
        Optional<User> userOpt = userRepository.findByUsername(usernameOrEmail);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(usernameOrEmail);
        }

        if (userOpt.isPresent() && encoder.matches(password, userOpt.get().getPassword())) {
            return userOpt.get();  // Return the authenticated user object
        }
        throw new RuntimeException("Invalid credentials");
    }

    @Override
    public void logout(HttpSession session) {
        if(session.getAttribute("user")!=null){
            session.invalidate();
            SecurityContextHolder.clearContext();
        }else {
            throw new RuntimeException("No user loggged in");
        }
    }
}
