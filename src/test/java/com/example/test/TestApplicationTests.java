package com.example.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class TestApplicationTests {

    @Test
    void contextLoads() {
        // Add some logging to help diagnose the issue
        System.out.println("Context is loading...");
    }
}