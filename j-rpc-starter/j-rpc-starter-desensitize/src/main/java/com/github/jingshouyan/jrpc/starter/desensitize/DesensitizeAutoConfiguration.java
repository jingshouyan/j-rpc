package com.github.jingshouyan.jrpc.starter.desensitize;

import com.github.jingshouyan.jrpc.base.util.desensitize.JsonMasking;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.annotation.Resource;

/**
 * @author jingshouyan
 * #date 2018/10/26 11:41
 */
@Slf4j
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@EnableConfigurationProperties(DesensitizeProperties.class)
public class DesensitizeAutoConfiguration implements ApplicationRunner {
    @Resource
    private DesensitizeProperties properties;


    @Override
    public void run(ApplicationArguments args) {
        JsonMasking.DEFAULT.addSetting(properties.getSettings());
        log.info("default desensitize settings: {}", properties.getSettings());
    }
}
