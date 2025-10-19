package za.ac.cput.factory;

import org.springframework.security.crypto.password.PasswordEncoder;
import za.ac.cput.domain.Admin;
import za.ac.cput.util.Helper;

public class AdminFactory {
    public static Admin buildAdmin(String firstName, String lastName,
                                   String email, String password,
                                   PasswordEncoder passwordEncoder) {

        if (Helper.isNullorEmpty(firstName) || Helper.isNullorEmpty(lastName) ||
                Helper.isNullorEmpty(email) || Helper.isNullorEmpty(password)) {
            return null;
        }

        if (!Helper.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // Encrypt the password
        String encryptedPassword = passwordEncoder.encode(password);

        return new Admin.Builder()
                .setFirstName(firstName)
                .setLastName(lastName)
                .setEmail(email)
                .setPassword(encryptedPassword)
                .setRole("ADMIN")
                .build();
    }
}