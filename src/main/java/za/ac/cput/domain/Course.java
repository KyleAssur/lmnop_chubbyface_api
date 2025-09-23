//package za.ac.cput.domain;
//
//import jakarta.persistence.*;
//import java.util.Arrays;
//import java.util.Objects;
//
//@Entity
//@Table
//public class Course {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String title;
//    private String description;
//
//    @Lob
//    @Column(length = 100000)
//    private byte[] image; // required like Van.image
//
//    protected Course() {}
//
//    public Course(Builder builder) {
//        this.id = builder.id;
//        this.title = builder.title;
//        this.description = builder.description;
//        this.image = builder.image;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public byte[] getImage() {
//        return image;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        Course course = (Course) o;
//        return Objects.equals(id, course.id)
//                && Objects.equals(title, course.title)
//                && Objects.equals(description, course.description)
//                && Arrays.equals(image, course.image);
//    }
//
//    @Override
//    public int hashCode() {
//        int result = Objects.hash(id, title, description);
//        result = 31 * result + Arrays.hashCode(image);
//        return result;
//    }
//
//    @Override
//    public String toString() {
//        return "Course{" +
//                "id=" + id +
//                ", title='" + title + '\'' +
//                ", description='" + description + '\'' +
//                ", image=" + Arrays.toString(image) +
//                '}';
//    }
//
//    public static class Builder {
//        private Long id;
//        private String title;
//        private String description;
//        private byte[] image;
//
//        public Builder setId(Long id) {
//            this.id = id;
//            return this;
//        }
//
//        public Builder setTitle(String title) {
//            this.title = title;
//            return this;
//        }
//
//        public Builder setDescription(String description) {
//            this.description = description;
//            return this;
//        }
//
//        public Builder setImage(byte[] image) {
//            this.image = image;
//            return this;
//        }
//
//        public Builder copy(Course course) {
//            this.id = course.id;
//            this.title = course.title;
//            this.description = course.description;
//            this.image = course.image;
//            return this;
//        }
//
//        public Course build() {
//            return new Course(this);
//        }
//    }
//}
package za.ac.cput.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String description;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;

    // Private constructor for Builder
    private Course(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.image = builder.image;
    }

    // Default constructor (required by JPA)
    public Course() {}

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public byte[] getImage() { return image; }

    // Setters (optional, can be removed if using Builder only)
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setImage(byte[] image) { this.image = image; }

    // Builder class
    public static class Builder {
        private Long id;
        private String title;
        private String description;
        private byte[] image;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setImage(byte[] image) {
            this.image = image;
            return this;
        }

        public Builder copy(Course course) {
            this.id = course.id;
            this.title = course.title;
            this.description = course.description;
            this.image = course.image;
            return this;
        }

        public Course build() {
            return new Course(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Course course = (Course) o;

        if (!id.equals(course.id)) return false;
        if (!title.equals(course.title)) return false;
        if (!description.equals(course.description)) return false;
        return java.util.Arrays.equals(image, course.image);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + java.util.Arrays.hashCode(image);
        return result;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", image=" + (image != null ? "[" + image.length + " bytes]" : "null") +
                '}';
    }
}