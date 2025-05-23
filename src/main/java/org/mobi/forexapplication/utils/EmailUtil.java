package org.mobi.forexapplication.utils;

public class EmailUtil {
    public static String buildResetPasswordEmail(String token) {
        String url = "http://localhost:4200/reset-password?token=" + token;
        return "Hi,\n\nClick the following link to reset your password:\n" + url +
                "\n\nIf you did not request a password reset, you can safely ignore this email.";
    }
}
