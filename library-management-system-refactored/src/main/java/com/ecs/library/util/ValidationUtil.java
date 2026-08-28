package com.ecs.library.util;

import java.util.regex.Pattern;

/**
 * Utilidades de validación transversal.
 * Aplica DRY evitando duplicar validaciones en controladores y paneles UI.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[0-9]{7,15}$");

    public static boolean isNullOrBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (isNullOrBlank(email)) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (isNullOrBlank(phone)) return true; // teléfono puede ser opcional
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidIsbn(String isbn) {
        if (isNullOrBlank(isbn)) return false;
        String clean = isbn.replaceAll("[- ]", "");
        return clean.length() == 10 || clean.length() == 13;
    }

    public static boolean isPositiveInteger(String str) {
        if (isNullOrBlank(str)) return false;
        try {
            int val = Integer.parseInt(str.trim());
            return val > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
