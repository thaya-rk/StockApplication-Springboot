package org.mobi.forexapplication.utils;

import java.time.LocalDateTime;

public class DateTimeUtil {

    public static LocalDateTime getExpiryTimeInMinutes(int minutes) {
        return LocalDateTime.now().plusMinutes(minutes);
    }

    public static boolean isExpired(LocalDateTime expiryTime) {
        return expiryTime.isBefore(LocalDateTime.now());
    }
}
