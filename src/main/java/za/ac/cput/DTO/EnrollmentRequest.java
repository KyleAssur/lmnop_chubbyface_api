package za.ac.cput.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnrollmentRequest {
    private String firstName;
    private String lastName;
    private String courseName;
}