package za.ac.cput.factory;

import za.ac.cput.domain.User;
import za.ac.cput.util.Helper;

public class UserFactory {

    public static User buildCustomer(String firstName, String lastName,
                                     String email, String password) {

        if (Helper.isNullorEmpty(firstName) || Helper.isNullorEmpty(lastName) ||
                Helper.isNullorEmpty(email) || Helper.isNullorEmpty(password)) {
            return null;
        }

        if (!Helper.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        return new User.Builder()
                .SetfirstName(firstName)
                .SetlastName(lastName)
                .SetEmail(email)
                .SetPassword(password)
                .SetRole("USER")
                .build();
    }
}
