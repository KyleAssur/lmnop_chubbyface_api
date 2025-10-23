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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            System.out.println("=== ENROLLMENT DEBUG START ===");
            System.out.println("Received enrollment request: " + request);
            System.out.println("First Name: " + request.getFirstName());
            System.out.println("Last Name: " + request.getLastName());
            System.out.println("Course Name: " + request.getCourseName());

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
            List<Course> allCourses = courseRepository.findAll();
            System.out.println("All courses in DB: " + allCourses.size());
            allCourses.forEach(c -> System.out.println(" - " + c.getTitle() + " (ID: " + c.getId() + ")"));

            Optional<Course> courseOpt = courseRepository.findByTitle(courseName);
            if (courseOpt.isEmpty()) {
                System.out.println("Course not found: " + courseName);
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
            List<User> allUsers = userRepository.findAll();
            System.out.println("All users in DB: " + allUsers.size());
            allUsers.forEach(u -> System.out.println(" - " + u.getFirstName() + " " + u.getLastName() + " (ID: " + u.getId() + ")"));

            Optional<User> userOpt = userRepository.findByFirstNameAndLastName(firstName, lastName);
            if (userOpt.isEmpty()) {
                System.out.println("Student not found: " + firstName + " " + lastName);
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
            boolean alreadyEnrolled = enrollmentRepository.existsByStudentAndCourse(student, course);
            System.out.println("Already enrolled: " + alreadyEnrolled);

            if (alreadyEnrolled) {
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

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Enrollment request submitted successfully!");
            response.put("enrollmentId", savedEnrollment.getId());
            response.put("status", savedEnrollment.getStatus().name());
            response.put("enrollment", enrollmentDTO);

            System.out.println("=== ENROLLMENT DEBUG END ===");
            return ResponseEntity.ok(response);

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

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Enrollment approved successfully");
            response.put("enrollment", dto);

            return ResponseEntity.ok(response);
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

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Enrollment rejected successfully");
            response.put("enrollment", dto);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error rejecting enrollment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error rejecting enrollment: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/reset")
    public ResponseEntity<?> resetEnrollment(@PathVariable Long id) {
        try {
            Enrollment reset = enrollmentService.resetEnrollment(id);
            EnrollmentDTO dto = enrollmentService.toDTO(reset);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Enrollment reset to pending successfully");
            response.put("enrollment", dto);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error resetting enrollment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error resetting enrollment: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateEnrollmentStatus(@PathVariable Long id, @RequestBody Map<String, String> statusRequest) {
        try {
            String newStatus = statusRequest.get("status");
            if (newStatus == null) {
                return ResponseEntity.badRequest().body("Status is required");
            }

            Enrollment enrollment = enrollmentRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Enrollment not found with id: " + id));

            try {
                Enrollment.Status statusEnum = Enrollment.Status.valueOf(newStatus.toUpperCase());
                enrollment.setStatus(statusEnum);
                Enrollment updated = enrollmentRepository.save(enrollment);
                EnrollmentDTO dto = enrollmentService.toDTO(updated);

                Map<String, Object> response = new HashMap<>();
                response.put("message", "Enrollment status updated to " + newStatus + " successfully");
                response.put("enrollment", dto);

                return ResponseEntity.ok(response);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid status: " + newStatus + ". Valid statuses: PENDING, APPROVED, REJECTED");
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error updating enrollment status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating enrollment status: " + e.getMessage());
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
}