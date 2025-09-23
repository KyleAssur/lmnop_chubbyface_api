//package za.ac.cput.factory;
//
//import za.ac.cput.domain.Course;
//import za.ac.cput.util.Helper;
//
//public class CourseFactory {
//    public static Course buildCourse(String title, String description, byte[] image) {
//        if (Helper.isNullorEmpty(title)
//                || Helper.isNullorEmpty(description)
//                || image == null
//                || image.length == 0) {
//            return null;
//        }
//
//        return new Course.Builder()
//                .setTitle(title)
//                .setDescription(description)
//                .setImage(image)
//                .build();
//    }
//}
package za.ac.cput.factory;

import za.ac.cput.domain.Course;
import za.ac.cput.util.Helper;

public class CourseFactory {
    public static Course buildCourse(String title, String description, byte[] image) {
        if (Helper.isNullorEmpty(title) || Helper.isNullorEmpty(description)) {
            return null;
        }

        return new Course.Builder()
                .setTitle(title)
                .setDescription(description)
                .setImage(image)
                .build();
    }

    public static Course createCourse(String title, String description) {
        return buildCourse(title, description, null);
    }

    public static Course createCourseWithId(Long id, String title, String description, byte[] image) {
        if (Helper.isNullorEmpty(title) || Helper.isNullorEmpty(description)) {
            return null;
        }

        return new Course.Builder()
                .setId(id)
                .setTitle(title)
                .setDescription(description)
                .setImage(image)
                .build();
    }
}