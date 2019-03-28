package com.github.jingshouyan.forward.starter;

import com.github.jingshouyan.forward.starter.aop.JrpcForward;
import com.github.jingshouyan.jrpc.base.action.ActionInterceptorHolder;
import com.github.jingshouyan.jrpc.client.JrpcClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author jingshouyan
 * #date 2019/1/12 11:16
 */

@Configuration
@EnableConfigurationProperties({ForwardProperties.class})
public class ForwardAutoConfiguration {

    @Resource
    private ForwardProperties properties;
    @Resource
    private JrpcClient client;

    @Bean
    @ConditionalOnMissingBean(JrpcForward.class)
    JrpcForward jrpcForward(){
        JrpcForward forward = new JrpcForward(client,properties);
        ActionInterceptorHolder.addServerInterceptor(forward);
        return forward;
    }

}
