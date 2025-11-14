package com.devops.course;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Main application
 */
@SpringBootTest
@ActiveProfiles("test")
class MainTest {

    @Test
    void contextLoads() {
        // Test that Spring context loads successfully
    }

    @Test
    void testJavaVersion() {
        String javaVersion = System.getProperty("java.version");
        assertNotNull(javaVersion);
        assertTrue(javaVersion.startsWith("21"),
            "Expected Java 21, but got: " + javaVersion);
    }
}
