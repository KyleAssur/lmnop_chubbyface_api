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

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Autowired
    public EnrollmentController(
            EnrollmentService enrollmentService,
            CourseRepository courseRepository,
            UserRepository userRepository,
            EnrollmentRepository enrollmentRepository
    ) {
        this.enrollmentService = enrollmentService;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @PostMapping("/enroll")
    public ResponseEntity<?> enrollStudent(@RequestBody EnrollmentRequest request) {
        try {
            System.out.println("Received enrollment request: " + request);

            // Validate input
            if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("First name is required");
            }
            if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Last name is required");
            }
            if (request.getCourseName() == null || request.getCourseName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Course name is required");
            }

            String firstName = request.getFirstName().trim();
            String lastName = request.getLastName().trim();
            String courseName = request.getCourseName().trim();

            // Find course by title
            System.out.println("Looking for course: " + courseName);
            Optional<Course> courseOpt = courseRepository.findByTitle(courseName);
            if (courseOpt.isEmpty()) {
                System.out.println("Course not found: " + courseName);
                // List available courses for debugging
                List<Course> allCourses = courseRepository.findAll();
                String availableCourses = allCourses.stream()
                        .map(Course::getTitle)
                        .collect(Collectors.joining(", "));
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Course '" + courseName + "' not found. Available courses: " + availableCourses);
            }
            Course course = courseOpt.get();
            System.out.println("Found course: " + course.getTitle() + " (ID: " + course.getId() + ")");

            // Find user by first and last name
            System.out.println("Looking for student: " + firstName + " " + lastName);
            Optional<User> userOpt = userRepository.findByFirstNameAndLastName(firstName, lastName);
            if (userOpt.isEmpty()) {
                System.out.println("Student not found: " + firstName + " " + lastName);
                // List available users for debugging
                List<User> allUsers = userRepository.findAll();
                String availableUsers = allUsers.stream()
                        .map(u -> u.getFirstName() + " " + u.getLastName())
                        .collect(Collectors.joining(", "));
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Student '" + firstName + " " + lastName + "' not found. Available students: " + availableUsers);
            }
            User student = userOpt.get();
            System.out.println("Found student: " + student.getFirstName() + " " + student.getLastName() + " (ID: " + student.getId() + ")");

            // Check if already enrolled
            System.out.println("Checking existing enrollment for student " + student.getId() + " in course " + course.getId());
            if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
                System.out.println("Student already enrolled in this course");
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("You are already enrolled in '" + courseName + "'");
            }

            // Create and save enrollment
            System.out.println("Creating new enrollment...");
            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(student);
            enrollment.setCourse(course);
            enrollment.setStatus(Enrollment.Status.PENDING);

            Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
            System.out.println("Enrollment created successfully with ID: " + savedEnrollment.getId());

            // Convert to DTO for response
            EnrollmentDTO enrollmentDTO = enrollmentService.toDTO(savedEnrollment);

            return ResponseEntity.ok(Map.of(
                    "message", "Enrollment request submitted successfully!",
                    "enrollmentId", savedEnrollment.getId(),
                    "status", savedEnrollment.getStatus().name(),
                    "enrollment", enrollmentDTO
            ));

        } catch (Exception e) {
            System.err.println("Enrollment error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Enrollment failed: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentDTO> getEnrollment(@PathVariable Long id) {
        try {
            Optional<Enrollment> enrollment = enrollmentService.read(id);
            return enrollment
                    .map(enr -> ResponseEntity.ok(enrollmentService.toDTO(enr)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("Error getting enrollment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<EnrollmentDTO>> getAllEnrollments() {
        try {
            List<EnrollmentDTO> enrollments = enrollmentService.getAllDTOs();
            return ResponseEntity.ok(enrollments);
        } catch (Exception e) {
            System.err.println("Error getting all enrollments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<EnrollmentDTO>> getEnrollmentsByStudent(@PathVariable Long studentId) {
        try {
            List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByStudentDTO(studentId);
            return ResponseEntity.ok(enrollments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error getting student enrollments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrollmentDTO>> getEnrollmentsByCourse(@PathVariable Long courseId) {
        try {
            List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByCourseDTO(courseId);
            return ResponseEntity.ok(enrollments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error getting course enrollments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EnrollmentDTO>> getEnrollmentsByStatus(@PathVariable String status) {
        try {
            Enrollment.Status statusEnum;
            try {
                statusEnum = Enrollment.Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }

            List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByStatusDTO(statusEnum);
            return ResponseEntity.ok(enrollments);
        } catch (Exception e) {
            System.err.println("Error getting enrollments by status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveEnrollment(@PathVariable Long id) {
        try {
            Enrollment approved = enrollmentService.approveEnrollment(id);
            EnrollmentDTO dto = enrollmentService.toDTO(approved);
            return ResponseEntity.ok(Map.of(
                    "message", "Enrollment approved successfully",
                    "enrollment", dto
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error approving enrollment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error approving enrollment: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectEnrollment(@PathVariable Long id) {
        try {
            Enrollment rejected = enrollmentService.rejectEnrollment(id);
            EnrollmentDTO dto = enrollmentService.toDTO(rejected);
            return ResponseEntity.ok(Map.of(
                    "message", "Enrollment rejected successfully",
                    "enrollment", dto
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error rejecting enrollment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error rejecting enrollment: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEnrollment(@PathVariable Long id) {
        try {
            enrollmentService.delete(id);
            return ResponseEntity.ok("Enrollment deleted successfully");
        } catch (Exception e) {
            System.err.println("Error deleting enrollment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting enrollment: " + e.getMessage());
        }
    }

    // Helper class for response
    private static class Map {
        public static java.util.Map<String, Object> of(Object... keyValues) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            for (int i = 0; i < keyValues.length; i += 2) {
                map.put((String) keyValues[i], keyValues[i + 1]);
            }
            return map;
        }
    }
}