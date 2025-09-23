package za.ac.cput.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.DTO.EnrollmentDTO;
import za.ac.cput.DTO.EnrollmentRequest;
import za.ac.cput.domain.Course;
import za.ac.cput.domain.Enrollment;
import za.ac.cput.domain.User;
import za.ac.cput.repository.CourseRepository;
import za.ac.cput.repository.EnrollmentRepository;
import za.ac.cput.repository.UserRepository;
import za.ac.cput.service.EnrollmentService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final CourseRepository courseRepository;
    private final UserRepository customerRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Autowired
    public EnrollmentController(
            EnrollmentService enrollmentService,
            CourseRepository courseRepository,
            UserRepository customerRepository,
            EnrollmentRepository enrollmentRepository
    ) {
        this.enrollmentService = enrollmentService;
        this.courseRepository = courseRepository;
        this.customerRepository = customerRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @PostMapping("/enroll")
    public ResponseEntity<?> enrollStudent(@RequestBody EnrollmentRequest request) {
        Optional<Course> courseOpt = courseRepository.findByTitle(request.getCourseName());
        if (courseOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Course with name '" + request.getCourseName() + "' not found.");
        }
        Course course = courseOpt.get();

        Optional<User> customerOpt = customerRepository.findByFirstNameAndLastName(
                request.getFirstName(), request.getLastName()
        );
        if (customerOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Customer not found.");
        }
        User customer = customerOpt.get();

        if (enrollmentRepository.existsByCustomerAndCourse(customer, course)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Customer is already enrolled in this course.");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setCustomer(customer);
        enrollment.setCourse(course);
        enrollment.setStatus(Enrollment.Status.PENDING);
        enrollmentRepository.save(enrollment);

        return ResponseEntity.ok("Enrollment successful.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentDTO> getEnrollment(@PathVariable Long id) {
        Optional<Enrollment> enrollment = enrollmentService.read(id);
        return enrollment
                .map(enr -> ResponseEntity.ok(enrollmentService.toDTO(enr)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<EnrollmentDTO> getAllEnrollments() {
        return enrollmentService.getAll()
                .stream()
                .map(enrollmentService::toDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<EnrollmentDTO> approveEnrollment(@PathVariable Long id) {
        Optional<Enrollment> enrollment = enrollmentService.read(id);
        if (enrollment.isPresent()) {
            Enrollment e = enrollment.get();
            e.setStatus(Enrollment.Status.APPROVED);
            Enrollment updated = enrollmentService.update(e);
            return ResponseEntity.ok(enrollmentService.toDTO(updated));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<EnrollmentDTO> rejectEnrollment(@PathVariable Long id) {
        Optional<Enrollment> enrollment = enrollmentService.read(id);
        if (enrollment.isPresent()) {
            Enrollment e = enrollment.get();
            e.setStatus(Enrollment.Status.REJECTED);
            Enrollment updated = enrollmentService.update(e);
            return ResponseEntity.ok(enrollmentService.toDTO(updated));
        }
        return ResponseEntity.notFound().build();
    }
}
