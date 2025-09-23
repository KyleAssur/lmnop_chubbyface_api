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
    private final UserRepository customerRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public EnrollmentService(
            EnrollmentRepository enrollmentRepository,
            UserRepository customerRepository,
            CourseRepository courseRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.customerRepository = customerRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public Enrollment create(Enrollment enrollment) {
        if (enrollment.getCustomer() == null || enrollment.getCustomer().getId() == null) {
            throw new IllegalArgumentException("Customer ID must be provided");
        }
        if (enrollment.getCourse() == null || enrollment.getCourse().getId() == null) {
            throw new IllegalArgumentException("Course ID must be provided");
        }

        // Fetch customer from DB by ID
        User customer = customerRepository.findById(enrollment.getCustomer().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer not found with ID: " + enrollment.getCustomer().getId()
                ));

        // Fetch course from DB by ID
        Course course = courseRepository.findById(enrollment.getCourse().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Course not found with ID: " + enrollment.getCourse().getId()
                ));

        // Check if already enrolled
        if (enrollmentRepository.existsByCustomerAndCourse(customer, course)) {
            throw new IllegalStateException("Customer is already enrolled in this course.");
        }

        // Create new enrollment with safe data
        Enrollment newEnrollment = new Enrollment();
        newEnrollment.setCustomer(customer);
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
        return enrollmentRepository.findByCustomer(customer);
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
        String fullName = (enrollment.getCustomer().getFirstName() + " " + enrollment.getCustomer().getLastName()).trim();
        return new EnrollmentDTO(
                enrollment.getId(),
                fullName,
                enrollment.getCourse().getTitle(),
                enrollment.getStatus().name()
        );
    }
    public List<EnrollmentDTO> getAllDTOs() {
        return enrollmentRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }


}
