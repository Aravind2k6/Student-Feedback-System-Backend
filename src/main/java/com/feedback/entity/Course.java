package com.feedback.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;   // e.g. "FSAD"

    @Column(nullable = false)
    private String code;   // e.g. "24SDC02E"

    @Column(nullable = false)
    private String courseName; // e.g. "Full Stack Application Development"

    @Column(nullable = false)
    private String instructor;

    private Integer credits;

    @Column(nullable = false)
    private boolean released = true;

    public Course() {}
    public Course(Long id, String name, String code, String courseName, String instructor, Integer credits, boolean released) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.courseName = courseName;
        this.instructor = instructor;
        this.credits = credits;
        this.released = released;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }
    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }
    public boolean isReleased() { return released; }
    public void setReleased(boolean released) { this.released = released; }
}
