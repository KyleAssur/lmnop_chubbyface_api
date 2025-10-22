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
            System.out.println("Received course creation request:");
            System.out.println("Title: " + title);
            System.out.println("Description: " + description);
            System.out.println("Image file: " + (imageFile != null ? imageFile.getOriginalFilename() + " (" + imageFile.getSize() + " bytes)" : "null"));

            if (imageFile == null || imageFile.isEmpty()) {
                return ResponseEntity.badRequest().body("Image file is required.");
            }

            // Validate file type
            String contentType = imageFile.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/jpg"))) {
                return ResponseEntity.badRequest().body("Only JPEG and PNG images are allowed.");
            }

            // Validate file size (max 5MB)
            if (imageFile.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("Image size must be less than 5MB.");
            }

            byte[] imageBytes = imageFile.getBytes();
            System.out.println("Image bytes length: " + imageBytes.length);

            Course course = CourseFactory.buildCourse(title, description, imageBytes);
            if (course == null) {
                return ResponseEntity.badRequest().body("Invalid input fields");
            }

            Course saved = courseService.create(course);
            System.out.println("Course created successfully with ID: " + saved.getId());

            return ResponseEntity.ok(saved);

        } catch (IOException e) {
            System.err.println("Image processing error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process image file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Course creation error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create course: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public List<Course> getAll() {
        return courseService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        try {
            Course course = courseService.read(id);
            if (course == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(course);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retrieving course: " + e.getMessage());
        }
    }

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> update(
            @RequestParam("id") Long id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        try {
            System.out.println("Received course update request for ID: " + id);
            System.out.println("Title: " + title);
            System.out.println("Description: " + description);
            System.out.println("Image file: " + (imageFile != null ? imageFile.getOriginalFilename() : "null"));

            Course existing = courseService.read(id);
            if (existing == null) {
                return ResponseEntity.notFound().build();
            }

            byte[] imageBytes = null;
            if (imageFile != null && !imageFile.isEmpty()) {
                // Validate file type
                String contentType = imageFile.getContentType();
                if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/jpg"))) {
                    return ResponseEntity.badRequest().body("Only JPEG and PNG images are allowed.");
                }

                // Validate file size (max 5MB)
                if (imageFile.getSize() > 5 * 1024 * 1024) {
                    return ResponseEntity.badRequest().body("Image size must be less than 5MB.");
                }

                imageBytes = imageFile.getBytes();
                System.out.println("New image bytes length: " + imageBytes.length);
            } else {
                // Keep existing image
                imageBytes = existing.getImage();
                System.out.println("Keeping existing image, length: " + (imageBytes != null ? imageBytes.length : 0));
            }

            // Use existing values if no new values are provided
            String updatedTitle = (title != null && !title.trim().isEmpty()) ? title : existing.getTitle();
            String updatedDescription = (description != null && !description.trim().isEmpty()) ? description : existing.getDescription();

            Course updatedCourse = courseService.updateWithImage(id, updatedTitle, updatedDescription, imageBytes);

            if (updatedCourse == null) {
                return ResponseEntity.badRequest().body("Failed to update course");
            }

            System.out.println("Course updated successfully: " + updatedCourse.getId());
            return ResponseEntity.ok(updatedCourse);

        } catch (IOException e) {
            System.err.println("Image processing error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process image file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Course update error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update course: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            boolean deleted = courseService.delete(id);
            if (!deleted) return ResponseEntity.notFound().build();
            return ResponseEntity.ok("Course deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting course: " + e.getMessage());
        }
    }

    @GetMapping("/ping")
    public String ping() {
        return "Course backend running";
    }

    @GetMapping("/media/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long id) {
        try {
            Course course = courseService.read(id);
            if (course == null) return ResponseEntity.notFound().build();

            byte[] file = course.getImage();
            if (file == null || file.length == 0) {
                return ResponseEntity.notFound().build();
            }

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
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchCourses(@RequestParam String keyword) {
        try {
            List<Course> courses = courseService.findByTitleContaining(keyword);
            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error searching courses: " + e.getMessage());
        }
    }
}