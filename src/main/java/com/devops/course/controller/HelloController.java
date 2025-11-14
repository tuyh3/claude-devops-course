package com.devops.course.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple REST controller to demonstrate Spring Boot Web functionality
 */
@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public Map<String, String> hello() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to Claude DevOps Course!");
        response.put("javaVersion", System.getProperty("java.version"));
        response.put("project", "Learning various languages, middleware and databases");
        return response;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("application", "claude-devops-course");
        return response;
    }
}