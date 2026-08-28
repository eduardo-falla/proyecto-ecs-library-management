package com.ecs.library.util;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateUtil {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static Date today() {
        return Date.valueOf(LocalDate.now());
    }

    public static Date addDays(Date baseDate, int days) {
        if (baseDate == null) return null;
        LocalDate local = baseDate.toLocalDate().plusDays(days);
        return Date.valueOf(local);
    }

    public static long daysBetween(Date start, Date end) {
        if (start == null || end == null) return 0;
        return ChronoUnit.DAYS.between(start.toLocalDate(), end.toLocalDate());
    }

    public static String format(Date date) {
        if (date == null) return "N/A";
        return DATE_FORMAT.format(date);
    }
}
