package com.github.jingshouyan.jrpc.starter.client;

import com.github.jingshouyan.jrpc.client.JrpcClient;
import com.github.jingshouyan.jrpc.client.pool.KeyedTransportPool;
import com.github.jingshouyan.jrpc.registry.NodeManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author jingshouyan
 * #date 2018/10/26 11:41
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(ClientProperties.class)
public class JrpcClientAutoConfiguration {
    @Resource
    private ClientProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public JrpcClient jrpcClient(KeyedTransportPool pool, NodeManager nodeManager) {

        return new JrpcClient(pool, nodeManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public KeyedTransportPool keyedTransportPool() {
        return new KeyedTransportPool(properties.getPool(), properties.getConnect());
    }
}
