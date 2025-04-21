package com.api.utils;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeUtil {
    public static String formatRelativeTime(LocalDateTime time) {
        Duration duration = Duration.between(time, LocalDateTime.now());

        long seconds = duration.getSeconds();
        if (seconds < 60) {
            return seconds + " giây";
        } else if (seconds < 3600) {
            return (seconds / 60) + " phút";
        } else if (seconds < 86400) {
            return (seconds / 3600) + " giờ";
        } else if (seconds < 2592000) { // 30 ngày
            return (seconds / 86400) + " ngày";
        } else if (seconds < 31536000) {
            return (seconds / 2592000) + " tháng";
        } else {
            return (seconds / 31536000) + " năm";
        }
    }

    public static String formatDurationFromSeconds(double totalSeconds) {
        long seconds = (long) totalSeconds;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;

        if (hours > 0 && minutes > 0) {
            return hours + " giờ " + minutes + " phút";
        } else if (hours > 0) {
            return hours + " giờ";
        } else {
            return minutes + " phút";
        }
    }

    public static String formatDistance(double distanceInMeters) {
        if (distanceInMeters < 1000) {
            return (int) distanceInMeters + " m";
        } else {
            return String.format("%.1f km", distanceInMeters / 1000);
        }
    }
}
