package org.mobi.forexapplication.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.Claims;
import org.mobi.forexapplication.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;




    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        super(new AuthenticationManager() {
            @Override
            public Authentication authenticate(Authentication authentication) {
                throw new UnsupportedOperationException("AuthenticationManager is not used here");
            }
        });
        this.jwtUtil = jwtUtil;
        this.userDetailsService=userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                // Validate the token's signature and expiration
                if (jwtUtil.validateToken(token)) {

                    // Extract username from token
                    String username = jwtUtil.getUsernameFromToken(token);

                    // Check if username exists and SecurityContext is empty
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                        // Load user details (from DB or in-memory store)
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                        // OPTIONAL: additional username match check (manual)
                        if (username.equals(userDetails.getUsername())) {

                            // Create authentication token
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails, null, userDetails.getAuthorities());

                            authentication.setDetails(
                                    new WebAuthenticationDetailsSource().buildDetails(request));

                            // Store it in the security context
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            System.out.println("JWT Authenticated User: " + authentication.getName());

                        }
                    }
                }
            } catch (Exception e) {
                // You can log or handle token errors here
            }
        }

        filterChain.doFilter(request, response);
    }

    private List<SimpleGrantedAuthority> extractAuthorities(Claims claims) {
        Object authoritiesClaim = claims.get("authorities");

        if (authoritiesClaim instanceof List<?> roles) {
            return roles.stream()
                    .filter(role -> role instanceof String)
                    .map(role -> new SimpleGrantedAuthority((String) role))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
