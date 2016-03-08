package com.tarpost.bryanty.proj_t_post.object;

/**
 * Created by BRYANTY on 08-Mar-16.
 */
public class Course {
    String courseId, facultyId, name;

    public Course() {
    }

    public Course(String courseId, String facultyId, String name) {
        this.courseId = courseId;
        this.facultyId = facultyId;
        this.name = name;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
