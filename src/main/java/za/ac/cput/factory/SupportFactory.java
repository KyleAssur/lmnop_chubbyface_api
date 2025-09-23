package za.ac.cput.factory;

import za.ac.cput.domain.Support;
import za.ac.cput.util.Helper;

public class SupportFactory {
    public static Support buildContactSupport(
            String firstName,
            String email,
            String message) {

        if (Helper.isNullorEmpty(firstName) || Helper.isNullorEmpty(email) || Helper.isNullorEmpty(message)) {
            return null;
        }

        if (!Helper.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        return new Support.Builder()
                .setFirstName(firstName)
                .setMessage(message)
                .build();
    }
}
