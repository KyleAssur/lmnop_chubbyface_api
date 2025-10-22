package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.DTO.EnrollmentDTO;
import za.ac.cput.domain.Enrollment;
import za.ac.cput.domain.Course;
import za.ac.cput.domain.User;
import za.ac.cput.repository.EnrollmentRepository;
import za.ac.cput.repository.UserRepository;
import za.ac.cput.repository.CourseRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService implements IEnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public EnrollmentService(
            EnrollmentRepository enrollmentRepository,
            UserRepository userRepository,
            CourseRepository courseRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public Enrollment create(Enrollment enrollment) {
        if (enrollment.getStudent() == null || enrollment.getStudent().getId() == null) {
            throw new IllegalArgumentException("Student ID must be provided");
        }
        if (enrollment.getCourse() == null || enrollment.getCourse().getId() == null) {
            throw new IllegalArgumentException("Course ID must be provided");
        }

        // Fetch student from DB by ID
        User student = userRepository.findById(enrollment.getStudent().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Student not found with ID: " + enrollment.getStudent().getId()
                ));

        // Fetch course from DB by ID
        Course course = courseRepository.findById(enrollment.getCourse().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Course not found with ID: " + enrollment.getCourse().getId()
                ));

        // Check if already enrolled
        if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
            throw new IllegalStateException("Student is already enrolled in this course.");
        }

        // Create new enrollment with safe data
        Enrollment newEnrollment = new Enrollment();
        newEnrollment.setStudent(student);
        newEnrollment.setCourse(course);
        newEnrollment.setStatus(Enrollment.Status.PENDING);

        return enrollmentRepository.save(newEnrollment);
    }

    @Override
    public Optional<Enrollment> read(Long id) {
        return enrollmentRepository.findById(id);
    }

    @Override
    public Enrollment update(Enrollment enrollment) {
        if (enrollment.getId() != null && enrollmentRepository.existsById(enrollment.getId())) {
            return enrollmentRepository.save(enrollment);
        }
        throw new IllegalArgumentException("Enrollment not found with id: " + enrollment.getId());
    }

    @Override
    public void delete(Long id) {
        enrollmentRepository.deleteById(id);
    }

    @Override
    public List<Enrollment> getAll() {
        return enrollmentRepository.findAll();
    }

    @Override
    public List<Enrollment> getEnrollmentsByCustomer(User customer) {
        return List.of();
    }

    @Override
    public List<Enrollment> getEnrollmentsByStudent(User student) {
        return enrollmentRepository.findByStudent(student);
    }

    @Override
    public List<Enrollment> getEnrollmentsByCourse(Course course) {
        return enrollmentRepository.findByCourse(course);
    }

    @Override
    public List<Enrollment> getEnrollmentsByStatus(Enrollment.Status status) {
        return enrollmentRepository.findByStatus(status);
    }

    @Override
    public Enrollment approveEnrollment(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found with id: " + id));
        enrollment.setStatus(Enrollment.Status.APPROVED);
        return enrollmentRepository.save(enrollment);
    }

    @Override
    public Enrollment rejectEnrollment(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found with id: " + id));
        enrollment.setStatus(Enrollment.Status.REJECTED);
        return enrollmentRepository.save(enrollment);
    }

    public EnrollmentDTO toDTO(Enrollment enrollment) {
        return new EnrollmentDTO(
                enrollment.getId(),
                enrollment.getStudent().getFirstName(),
                enrollment.getStudent().getLastName(),
                enrollment.getCourse().getTitle(),
                enrollment.getStatus().name(),
                enrollment.getEnrollmentDate()
        );
    }

    public List<EnrollmentDTO> getAllDTOs() {
        return enrollmentRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<EnrollmentDTO> getEnrollmentsByStudentDTO(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + studentId));
        return enrollmentRepository.findByStudent(student)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<EnrollmentDTO> getEnrollmentsByCourseDTO(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));
        return enrollmentRepository.findByCourse(course)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<EnrollmentDTO> getEnrollmentsByStatusDTO(Enrollment.Status status) {
        return enrollmentRepository.findByStatus(status)
                .stream()
                .map(this::toDTO)
                .toList();
    }
}