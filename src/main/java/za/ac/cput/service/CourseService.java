package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.Course;
import za.ac.cput.repository.CourseRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course create(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        return courseRepository.save(course);
    }

    public Course read(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Course ID cannot be null");
        }
        Optional<Course> course = courseRepository.findById(id);
        return course.orElse(null);
    }

    public Course update(Course course) {
        if (course == null || course.getId() == null) {
            throw new IllegalArgumentException("Course and Course ID cannot be null");
        }

        if (!courseRepository.existsById(course.getId())) {
            return null;
        }

        // Get existing course
        Course existing = read(course.getId());
        if (existing == null) {
            return null;
        }

        // Update only the fields that are provided and changed
        Course.Builder builder = new Course.Builder()
                .copy(existing);

        if (course.getTitle() != null && !course.getTitle().equals(existing.getTitle())) {
            builder.setTitle(course.getTitle());
        }

        if (course.getDescription() != null && !course.getDescription().equals(existing.getDescription())) {
            builder.setDescription(course.getDescription());
        }

        if (course.getImage() != null && course.getImage().length > 0) {
            builder.setImage(course.getImage());
        }

        Course updatedCourse = builder.build();
        return courseRepository.save(updatedCourse);
    }

    public Course updateWithImage(Long id, String title, String description, byte[] image) {
        if (id == null) {
            throw new IllegalArgumentException("Course ID cannot be null");
        }

        Course existing = read(id);
        if (existing == null) {
            return null;
        }

        Course.Builder builder = new Course.Builder()
                .copy(existing);

        if (title != null && !title.trim().isEmpty()) {
            builder.setTitle(title);
        }

        if (description != null && !description.trim().isEmpty()) {
            builder.setDescription(description);
        }

        if (image != null && image.length > 0) {
            builder.setImage(image);
        }

        return courseRepository.save(builder.build());
    }

    public boolean delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Course ID cannot be null");
        }

        if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Course> getAll() {
        return courseRepository.findAll();
    }

    public Course findByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Course title cannot be null or empty");
        }
        Optional<Course> course = courseRepository.findByTitle(title);
        return course.orElse(null);
    }

    public boolean existsById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Course ID cannot be null");
        }
        return courseRepository.existsById(id);
    }

    public List<Course> findByTitleContaining(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        // This would require adding a method to your repository
        // For now, we'll filter manually
        return getAll().stream()
                .filter(course -> course.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }
}