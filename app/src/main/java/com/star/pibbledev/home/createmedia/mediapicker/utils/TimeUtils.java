package com.star.pibbledev.home.createmedia.mediapicker.utils;

public class TimeUtils {
    public static String formatCountDownTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;

        seconds = seconds % 60;
        minutes = minutes % 60;

        String sec = String.valueOf(seconds);
        String min = String.valueOf(minutes);

        if (seconds < 10) {
            sec = "0" + seconds;
        }

        if (minutes < 10) {
            min = "0" + minutes;
        }

        return min + ":" + sec;
    }
}
