package com.jing.test.client;

import com.github.jingshouyan.jrpc.starter.client.EnableJrpcServices;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author jingshouyan
 * #date 2018/10/26 12:03
 */
@SpringBootApplication
@EnableScheduling
@EnableJrpcServices
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
