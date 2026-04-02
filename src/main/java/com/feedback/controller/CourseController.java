package com.feedback.controller;

import com.feedback.entity.Course;
import com.feedback.service.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/released")
    public List<Course> getReleasedCourses() {
        return courseService.getReleasedCourses();
    }

    @PatchMapping("/{name}/release")
    public Course toggleRelease(@PathVariable String name) {
        return courseService.toggleRelease(name);
    }
}
