package za.ac.cput.service;

import za.ac.cput.domain.Course;
import java.util.List;
import java.util.Optional;

public interface ICourseService {
    Course create(Course course);
    Optional<Course> read(Long id);
    Course update(Course course);
    void delete(Long id);
    List<Course> getAll();
}
