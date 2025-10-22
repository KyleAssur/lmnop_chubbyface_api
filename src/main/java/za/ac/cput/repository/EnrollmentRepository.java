package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.Course;
import za.ac.cput.domain.Enrollment;
import za.ac.cput.domain.User;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByStudentAndCourse(User student, Course course);  // Changed from customer to student

    List<Enrollment> findByStudent(User student);  // Changed from customer to student

    List<Enrollment> findByCourse(Course course);

    List<Enrollment> findByStatus(Enrollment.Status status);
}
