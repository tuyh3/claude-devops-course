package com.devops.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Claude DevOps Course - Main Application
 * 一个用 Claude Code 辅助学习各种语言、中间件和数据库的项目
 */
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        System.out.println("Welcome to Claude DevOps Course!");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("Project: Learning various languages, middleware and databases");
    }
}
