package com.feedback.service;

import com.feedback.entity.Course;
import com.feedback.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> getReleasedCourses() {
        return courseRepository.findByReleasedTrue();
    }

    public Course toggleRelease(String courseName) {
        Course course = courseRepository.findByName(courseName)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseName));
        course.setReleased(!course.isReleased());
        return courseRepository.save(course);
    }

    public Course save(Course course) {
        return courseRepository.save(course);
    }
}
