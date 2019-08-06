package com.jing.test;

import com.github.jingshouyan.jrpc.starter.trace.TraceProperties;
import com.github.jingshouyan.jrpc.starter.trace.interceptor.ServerTrace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author jingshouyan
 * 11/29/18 5:26 PM
 */
@SpringBootApplication
@EnableScheduling
@Slf4j
public class TestApp {
    public static void main(String[] args) {
        SpringApplication.run(TestApp.class, args);
    }

    @Autowired
    private ApplicationContext ctx;
    @Scheduled(fixedRate = 5000)
    public void job(){
        ServerTrace serverTrace = ctx.getBean(ServerTrace.class);
        System.out.println(serverTrace.getProperties());
        TraceProperties traceProperties = ctx.getBean(TraceProperties.class);
        System.out.println(traceProperties);
    }
}