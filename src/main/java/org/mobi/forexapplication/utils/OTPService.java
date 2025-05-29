package org.mobi.forexapplication.utils;

import org.mobi.forexapplication.model.PasswordResetOTP;
import org.mobi.forexapplication.model.User;
import org.mobi.forexapplication.repository.PasswordResetOtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OTPService {

    @Autowired
    private PasswordResetOtpRepository otpRepository;

    @Autowired
    private JavaMailSender mailSender;

    public String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    public void clearOtpForUser(User user) {
        otpRepository.deleteByUser(user);
        otpRepository.flush();
    }

    public PasswordResetOTP createAndSaveOtp(User user, String purpose) {
        String otp = generateOtp();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);

        PasswordResetOTP entity = new PasswordResetOTP(otp, user, expiry);
        entity.setPurpose(purpose);
        return otpRepository.save(entity);
    }

    public void sendOtpEmail(User user, String subject, String bodyPrefix, String otp) {
        String messageBody = bodyPrefix + otp + "\n\nThis OTP will expire in 15 minutes.";
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("demostockapp@gmail.com");
        message.setTo(user.getEmail());
        message.setSubject(subject);
        message.setText(messageBody);
        mailSender.send(message);
    }

    public void validateOtp(User user, String otp, String purpose) {
        PasswordResetOTP savedOtp = otpRepository.findByUserAndOtp(user, otp)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        if (savedOtp.isUsed())
            throw new RuntimeException("OTP has already been used");

        if (savedOtp.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new RuntimeException("OTP expired");

        // Optional: check purpose
        if (!purpose.equals(savedOtp.getPurpose())) {
            throw new RuntimeException("OTP purpose mismatch");
        }

        savedOtp.setUsed(true);
        otpRepository.save(savedOtp);
    }
}
