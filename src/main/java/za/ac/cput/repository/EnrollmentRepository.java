package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.Course;
import za.ac.cput.domain.Enrollment;
import za.ac.cput.domain.User;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByCustomerAndCourse(User customer, Course course);

    List<Enrollment> findByCustomer(User customer);

    List<Enrollment> findByCourse(Course course);

    List<Enrollment> findByStatus(Enrollment.Status status);
}

