package org.mobi.forexapplication.dto;


public class UserDTO {

    private String username;
    private String email;
    private String role;
    private boolean emailVerified;

    public UserDTO(String username, String email,String role,boolean emailVerified) {
        this.username = username;
        this.email = email;
        this.role=role;
        this.emailVerified = emailVerified;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole(){return role;}

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

}
