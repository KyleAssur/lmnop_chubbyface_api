package za.ac.cput.util;

import java.util.UUID;
import java.util.regex.Pattern;

public class Helper {
    public static boolean isNullorEmpty(String s) {
        return (s == null || s.equals("") || s.isEmpty() || s.equalsIgnoreCase("null"));
    }

    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    public static boolean isValidEmail(String email) {
        String regexPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.compile(regexPattern).matcher(email).matches();
    }
    public static void checkStringParam(String paramName, String paramValue) {
        if (isNullorEmpty(paramValue))
            throw new IllegalArgumentException(String.format("Invalid value for param: %s", paramName));
    }

    public static boolean isNullOrEmpty(String firstName) {
        return (firstName == null || firstName.isEmpty());

    }
}


