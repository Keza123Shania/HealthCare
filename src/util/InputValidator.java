package util;

import java.util.regex.Pattern;

public class InputValidator {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s.'-]+$");
    private static final Pattern ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");
    private static final Pattern SPECIALIZATION_PATTERN = Pattern.compile("^[a-zA-Z\\s&-]+$");

    public static String validateName(String name, String fieldName) {
        String trimmed = validateText(name, fieldName);
        if (!NAME_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException(fieldName + " must contain only letters, spaces, periods, hyphens, and apostrophes.");
        }
        return trimmed;
    }

    public static String validateId(String id, String fieldName) {
        String trimmed = validateText(id, fieldName);
        if (!ID_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException(fieldName + " must contain only letters, numbers, hyphens, and underscores.");
        }
        return trimmed;
    }

    public static String validateSpecialization(String specialization, String fieldName) {
        String trimmed = validateText(specialization, fieldName);
        if (!SPECIALIZATION_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException(fieldName + " must contain only letters, spaces, ampersands, and hyphens.");
        }
        return trimmed;
    }

    public static String validateText(String text, String fieldName) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return text.trim();
    }
}