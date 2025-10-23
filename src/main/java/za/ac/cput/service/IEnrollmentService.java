package za.ac.cput.service;

import za.ac.cput.domain.Course;
import za.ac.cput.domain.Enrollment;
import za.ac.cput.domain.User;

import java.util.List;
import java.util.Optional;

public interface IEnrollmentService {
    Enrollment create(Enrollment enrollment);

    Optional<Enrollment> read(Long id);

    Enrollment update(Enrollment enrollment);

    void delete(Long id);

    List<Enrollment> getAll();

    List<Enrollment> getEnrollmentsByStudent(User student);

    List<Enrollment> getEnrollmentsByCourse(Course course);

    List<Enrollment> getEnrollmentsByStatus(Enrollment.Status status);

    Enrollment approveEnrollment(Long id);

    Enrollment rejectEnrollment(Long id);

    // ADD THIS METHOD
    Enrollment resetEnrollment(Long id);
}