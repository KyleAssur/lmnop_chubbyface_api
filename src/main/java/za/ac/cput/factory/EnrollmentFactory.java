package za.ac.cput.factory;

import za.ac.cput.domain.Course;
import za.ac.cput.domain.Enrollment;
import za.ac.cput.domain.User;

public class EnrollmentFactory {
    public static Enrollment createEnrollment(User customer, Course course) {
        if (customer == null || course == null) {
            throw new IllegalArgumentException("Customer and Course must not be null");
        }

        return new Enrollment(customer, course, Enrollment.Status.PENDING);
    }

}
