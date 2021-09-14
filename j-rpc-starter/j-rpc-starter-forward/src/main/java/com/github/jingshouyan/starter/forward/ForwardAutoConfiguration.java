package com.github.jingshouyan.starter.forward;

import com.github.jingshouyan.jrpc.base.action.ActionInterceptorHolder;
import com.github.jingshouyan.jrpc.client.JrpcClient;
import com.github.jingshouyan.jrpc.starter.server.ServerProperties;
import com.github.jingshouyan.starter.forward.aop.JrpcForward;
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

    @Resource
    private ServerProperties serverProperties;

    @Bean
    @ConditionalOnMissingBean(JrpcForward.class)
    JrpcForward jrpcForward() {
        JrpcForward forward = new JrpcForward(client, properties, serverProperties.getVersion());
        ActionInterceptorHolder.addServerInterceptor(forward);
        return forward;
    }

}
