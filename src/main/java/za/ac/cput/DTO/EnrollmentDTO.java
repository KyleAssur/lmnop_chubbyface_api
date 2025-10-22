package za.ac.cput.DTO;

import java.time.LocalDateTime;

public class EnrollmentDTO {
    private Long id;
    private String studentFirstName;  // This matches frontend expectation
    private String studentLastName;   // This matches frontend expectation
    private String courseTitle;
    private String status;
    private LocalDateTime enrollmentDate;

    public EnrollmentDTO() {}

    public EnrollmentDTO(Long id, String studentFirstName, String studentLastName,
                         String courseTitle, String status, LocalDateTime enrollmentDate) {
        this.id = id;
        this.studentFirstName = studentFirstName;
        this.studentLastName = studentLastName;
        this.courseTitle = courseTitle;
        this.status = status;
        this.enrollmentDate = enrollmentDate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentFirstName() { return studentFirstName; }
    public void setStudentFirstName(String studentFirstName) { this.studentFirstName = studentFirstName; }

    public String getStudentLastName() { return studentLastName; }
    public void setStudentLastName(String studentLastName) { this.studentLastName = studentLastName; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDateTime enrollmentDate) { this.enrollmentDate = enrollmentDate; }
}