package com.eventmgmt.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateTimeUtil {

    private DateTimeUtil() {
    }

    public static boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }
        try {
            String[] parts = dateStr.trim().split("-");
            if (parts.length != 3) {
                return false;
            }
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            // Basic range validation for safety
            return year >= 2000 && (month >= 1 && month <= 12) && (day >= 1 && day <= 31);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return false;
        }
        try {
            String[] parts = timeStr.trim().split(":");
            if (parts.length != 2) {
                return false;
            }
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            // Basic 24-hour range validation
            return (hour >= 0 && hour <= 23) && (minute >= 0 && minute <= 59);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String formatDate(String dateStr) {
        return isValidDate(dateStr) ? dateStr.trim() : "2026-06-01";
    }

    public static String formatTime(String timeStr) {
        return isValidTime(timeStr) ? timeStr.trim() : "09:00";
    }

    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }
}
