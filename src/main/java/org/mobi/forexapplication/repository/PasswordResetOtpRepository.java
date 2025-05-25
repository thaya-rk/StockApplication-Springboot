package org.mobi.forexapplication.repository;

import org.mobi.forexapplication.model.PasswordResetOTP;
import org.mobi.forexapplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOTP, Long> {

    Optional<PasswordResetOTP> findByUserAndOtp(User user, String otp);

    void deleteByUser(User user);
}
