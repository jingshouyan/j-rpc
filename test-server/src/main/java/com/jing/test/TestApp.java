package com.jing.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TestApp {
    public static void main(String[] args) {
        SpringApplication.run(TestApp.class);
    }
}