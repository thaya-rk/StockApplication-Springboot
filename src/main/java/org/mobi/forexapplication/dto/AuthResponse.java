package org.mobi.forexapplication.dto;

public class AuthResponse {
    // Getters and Setters
    private final String username;

    private final String message;

    public AuthResponse(String message, String username) {
        this.message = message;
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }




}
