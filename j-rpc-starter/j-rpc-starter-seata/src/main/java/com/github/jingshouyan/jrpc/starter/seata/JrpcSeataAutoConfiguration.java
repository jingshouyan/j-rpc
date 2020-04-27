package com.github.jingshouyan.jrpc.starter.seata;

import com.github.jingshouyan.jrpc.base.action.ActionInterceptorHolder;
import com.github.jingshouyan.jrpc.starter.seata.interceptor.ClientSeatInterceptor;
import com.github.jingshouyan.jrpc.starter.seata.interceptor.ServerSeataInterceptor;
import io.seata.spring.annotation.GlobalTransactionScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
    public GlobalTransactionScanner scanner() {
        String applicationId = seataProperties.getApplicationId();
        String txServiceGroup = seataProperties.getTxServiceGroup();
        GlobalTransactionScanner scanner = new GlobalTransactionScanner(applicationId, txServiceGroup);
        return scanner;
    }

    @Bean
    @ConditionalOnMissingBean(ClientSeatInterceptor.class)
    public ClientSeatInterceptor clientSeatInterceptor() {
        ClientSeatInterceptor clientSeatInterceptor = new ClientSeatInterceptor();
        ActionInterceptorHolder.addClientInterceptor(clientSeatInterceptor);
        return clientSeatInterceptor;
    }

    @Bean
    @ConditionalOnMissingBean(ServerSeataInterceptor.class)
    public ServerSeataInterceptor serverSeataInterceptor() {
        ServerSeataInterceptor serverSeataInterceptor = new ServerSeataInterceptor();
        ActionInterceptorHolder.addServerInterceptor(serverSeataInterceptor);
        return serverSeataInterceptor;
    }


}
