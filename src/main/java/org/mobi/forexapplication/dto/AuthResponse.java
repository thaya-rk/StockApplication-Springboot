package org.mobi.forexapplication.dto;

public class AuthResponse {
    // Getters and Setters
    private final String username;
    private String token;
    private final String message;

    public AuthResponse(String message, String token, String username) {
        this.message = message;
        this.token = token;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }




}
