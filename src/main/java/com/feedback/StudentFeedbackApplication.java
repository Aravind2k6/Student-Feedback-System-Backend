package com.feedback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StudentFeedbackApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudentFeedbackApplication.class, args);
    }
}
