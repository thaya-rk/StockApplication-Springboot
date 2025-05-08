package org.mobi.forexapplication.dto;

public class AuthResponse {
    // Getters and Setters
    private final String message;

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

    private final String username;

    public AuthResponse(String message, String username) {
        this.message = message;
        this.username = username;
    }

}
