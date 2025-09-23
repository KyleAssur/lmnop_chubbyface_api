//package za.ac.cput.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import za.ac.cput.domain.Course;
//
//import za.ac.cput.repository.CourseRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class CourseService implements ICourseService {
//
//    private final CourseRepository repository;
//
//    @Autowired
//    public CourseService(CourseRepository repository) {
//        this.repository = repository;
//    }
//
//    @Override
//    public Course create(Course course) {
//        return repository.save(course);
//    }
//
//    @Override
//    public Optional<Course> read(Long id) {
//        return repository.findById(id);
//    }
//
//    @Override
//    public Course update(Course course) {
//        if (course.getId() != null && repository.existsById(course.getId())) {
//            return repository.save(course);
//        }
//        return null;
//    }
//
//    @Override
//    public void delete(Long id) {
//        repository.deleteById(id);
//    }
//
//    @Override
//    public List<Course> getAll() {
//        return repository.findAll();
//    }
//}
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
        return courseRepository.save(course);
    }

    public Course read(Long id) {
        Optional<Course> course = courseRepository.findById(id);
        return course.orElse(null);
    }

    public Course update(Course course) {
        if (courseRepository.existsById(course.getId())) {
            // Use Builder pattern to update
            Course existing = read(course.getId());
            Course updated = new Course.Builder()
                    .copy(existing)
                    .setTitle(course.getTitle())
                    .setDescription(course.getDescription())
                    .setImage(course.getImage())
                    .build();
            return courseRepository.save(updated);
        }
        return null;
    }

    public boolean delete(Long id) {
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
        Optional<Course> course = courseRepository.findByTitle(title);
        return course.orElse(null);
    }
}