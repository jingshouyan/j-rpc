package com.github.jingshouyan.jrpc.starter.seata;

import io.seata.spring.annotation.GlobalTransactionScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.annotation.Resource;

/**
 * @author jingshouyan
 * #date 2019/9/25 17:06
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({SeataProperties.class})
@Order(10)
public class JrpcSeataAutoConfiguration {

    @Resource
    private SeataProperties seataProperties;

    @Bean
    public GlobalTransactionScanner scanner(){
        String applicationId = seataProperties.getApplicationId();
        String txServiceGroup = seataProperties.getTxServiceGroup();
        GlobalTransactionScanner scanner = new GlobalTransactionScanner(applicationId,txServiceGroup);
        return scanner;
    }

}
