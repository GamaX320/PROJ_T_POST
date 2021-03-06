package com.tarpost.bryanty.proj_t_post.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by BRYANTY on 17-Jan-2016.
 */
public class User implements Parcelable {

    String userId, name, email, password, phoneNo, avatarUrl, coverUrl, faculty, course,
            description, gender, status, following;
    String facultyId, facultyName;
    String courseId, courseName;

    private Date createDateTime, updateDateTime;

    public User() {
    }

    protected User(Parcel in) {
        userId = in.readString();
        name = in.readString();
        email = in.readString();
        password = in.readString();
        phoneNo = in.readString();
        avatarUrl = in.readString();
        coverUrl = in.readString();
        faculty = in.readString();
        course = in.readString();
        description = in.readString();
        gender = in.readString();
        status = in.readString();
        following = in.readString();
        facultyId = in.readString();
        facultyName = in.readString();
        courseId = in.readString();
        courseName = in.readString();

    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public Date getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Date createDateTime) {
        this.createDateTime = createDateTime;
    }

    public Date getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(Date updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(phoneNo);
        dest.writeString(avatarUrl);
        dest.writeString(coverUrl);
        dest.writeString(faculty);
        dest.writeString(course);
        dest.writeString(description);
        dest.writeString(gender);
        dest.writeString(status);
        dest.writeString(following);
        dest.writeString(facultyId);
        dest.writeString(facultyName);
        dest.writeString(courseId);
        dest.writeString(courseName);
    }
}
