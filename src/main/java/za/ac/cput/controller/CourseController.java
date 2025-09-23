//package za.ac.cput.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import za.ac.cput.domain.Course;
//import za.ac.cput.factory.CourseFactory;
//import za.ac.cput.service.CourseService;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Optional;
//
//@CrossOrigin(origins = "http://localhost:5173")
//@RestController
//@RequestMapping("/courses")
//public class CourseController {
//
//    private final CourseService courseService;
//
//    @Autowired
//    public CourseController(CourseService courseService) {
//        this.courseService = courseService;
//    }
//
//    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> create(
//            @RequestParam("title") String title,
//            @RequestParam("description") String description,
//            @RequestParam("image") MultipartFile imageFile
//    ) {
//        try {
//            if (imageFile.isEmpty()) {
//                return ResponseEntity.badRequest().body("Image file is required.");
//            }
//
//            byte[] imageBytes = imageFile.getBytes();
//
//            Course course = CourseFactory.buildCourse(title, description, imageBytes);
//            if (course == null) {
//                return ResponseEntity.badRequest().body("Invalid input fields");
//            }
//
//            Course saved = courseService.create(course);
//            return ResponseEntity.ok(saved);
//        } catch (IOException e) {
//            return ResponseEntity.status(500).body("Failed to process image file.");
//        }
//    }
//
//    @GetMapping("/all")
//    public List<Course> getAll() {
//        return courseService.getAll();
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<?> get(@PathVariable Long id) {
//        Optional<Course> c = courseService.read(id);
//        if (c.isEmpty()) return ResponseEntity.notFound().build();
//        return ResponseEntity.ok(c);
//    }
//
//    @PutMapping("/update")
//    public ResponseEntity<?> update(@RequestBody Course input) {
//        Course updated = courseService.update(input);
//        if (updated == null) return ResponseEntity.badRequest().body("Course does not exist or invalid");
//        return ResponseEntity.ok(updated);
//    }
//
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<?> delete(@PathVariable Long id) {
//        courseService.delete(id);
//        return ResponseEntity.ok("Deleted");
//    }
//
//    @GetMapping("/ping")
//    public String ping() {
//        return "Course backend running";
//    }
//    @GetMapping("/media/{id}")
//    public ResponseEntity<byte[]> getFile(@PathVariable Long id) {
//        Optional<Course> courseOpt = courseService.read(id);
//        if (courseOpt.isEmpty()) return ResponseEntity.notFound().build();
//
//        Course course = courseOpt.get();
//        byte[] file = course.getImage();
//
//        HttpHeaders headers = new HttpHeaders();
//
//        // Option 1: detect type by first few bytes (simple)
//        if (file.length > 4 && file[0] == (byte)0xFF && file[1] == (byte)0xD8) {
//            headers.setContentType(MediaType.IMAGE_JPEG);
//        } else if (file.length > 4 && file[0] == (byte)0x89 && file[1] == (byte)0x50) {
//            headers.setContentType(MediaType.IMAGE_PNG);
//        } else {
//            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//        }
//
//        return new ResponseEntity<>(file, headers, HttpStatus.OK);
//    }
//
//
//}
package za.ac.cput.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import za.ac.cput.domain.Course;
import za.ac.cput.factory.CourseFactory;
import za.ac.cput.service.CourseService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("image") MultipartFile imageFile) {

        try {
            if (imageFile.isEmpty()) {
                return ResponseEntity.badRequest().body("Image file is required.");
            }

            byte[] imageBytes = imageFile.getBytes();

            Course course = CourseFactory.buildCourse(title, description, imageBytes);
            if (course == null) {
                return ResponseEntity.badRequest().body("Invalid input fields");
            }

            Course saved = courseService.create(course);
            return ResponseEntity.ok(saved);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to process image file.");
        }
    }

    @GetMapping("/all")
    public List<Course> getAll() {
        return courseService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Course course = courseService.read(id);
        if (course == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(course);
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody Course input) {
        Course updated = courseService.update(input);
        if (updated == null) return ResponseEntity.badRequest().body("Course does not exist or invalid");
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean deleted = courseService.delete(id);
        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Course deleted successfully");
    }

    @GetMapping("/ping")
    public String ping() {
        return "Course backend running";
    }

    @GetMapping("/media/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long id) {
        Course course = courseService.read(id);
        if (course == null) return ResponseEntity.notFound().build();

        byte[] file = course.getImage();
        HttpHeaders headers = new HttpHeaders();

        // Detect image type
        if (file.length > 4 && file[0] == (byte)0xFF && file[1] == (byte)0xD8) {
            headers.setContentType(MediaType.IMAGE_JPEG);
        } else if (file.length > 4 && file[0] == (byte)0x89 && file[1] == (byte)0x50) {
            headers.setContentType(MediaType.IMAGE_PNG);
        } else {
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        }

        return new ResponseEntity<>(file, headers, HttpStatus.OK);
    }
}