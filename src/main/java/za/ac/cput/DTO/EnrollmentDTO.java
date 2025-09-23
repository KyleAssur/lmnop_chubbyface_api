package za.ac.cput.DTO;

public class EnrollmentDTO {

    private Long id;
    private String customerName;  // full name: first + last
    private String courseTitle;
    private String status;

    public EnrollmentDTO() {}

    public EnrollmentDTO(Long id, String customerName, String courseTitle, String status) {
        this.id = id;
        this.customerName = customerName;
        this.courseTitle = courseTitle;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public String getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
