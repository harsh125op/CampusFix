package com.example.campusfix.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a", Locale.US);
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.US);

    public static String formatDate(long timestamp) {
        if (timestamp <= 0) return "N/A";
        return DATE_FORMAT.format(new Date(timestamp));
    }

    public static String formatTime(long timestamp) {
        if (timestamp <= 0) return "N/A";
        return TIME_FORMAT.format(new Date(timestamp));
    }

    public static String formatDateTime(long timestamp) {
        if (timestamp <= 0) return "N/A";
        return DATE_TIME_FORMAT.format(new Date(timestamp));
    }

    public static String generateUniqueReportId() {
        SimpleDateFormat idFormat = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US);
        int randomDigits = (int) (Math.random() * 900) + 100;
        return "CF-" + idFormat.format(new Date()) + "-" + randomDigits;
    }
}
