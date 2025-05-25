package org.mobi.forexapplication.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "users")
public class User  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, length = 30, unique = true)
    private String username;

    @Column(nullable = false, length = 30, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(length = 4)
    private String mpin;

    @Column(nullable = false)
    private String role = "USER"; // or "ADMIN"

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Timestamp createdAt;

    @Column(nullable = false,length = 50)
    private String fullName;

    @Column(length = 15)
    private String mobileNumber;

    @Column
    private Date dob;

    @Column(name = "email_verified")
    private boolean emailVerified = false;

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    @Column(nullable = false,precision = 12,scale = 2)
    private BigDecimal dematBalance=BigDecimal.ZERO;


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public BigDecimal getDematBalance() {
        return dematBalance;
    }

    public void setDematBalance(BigDecimal dematBalance) {
        this.dematBalance = dematBalance;
    }

    //getters and setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMpin() {
        return mpin;
    }

    public void setMpin(String mpin) {
        this.mpin = mpin;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    //to store datetime in minute values instead of milliseconds
    @PrePersist
    protected void onCreate() {
        this.createdAt = Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
    }
}
