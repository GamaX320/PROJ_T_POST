package com.tarpost.bryanty.proj_t_post.object;

/**
 * Created by BRYANTY on 06-Mar-16.
 */
public class Faculty {
    String facultyId, name;

    public Faculty() {
    }

    public Faculty(String facultyId, String name) {
        this.facultyId = facultyId;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }
}
