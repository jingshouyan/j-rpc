package com.github.jingshouyan.jrpc.starter.client;

import com.github.jingshouyan.jrpc.client.JrpcClient;
import com.github.jingshouyan.jrpc.client.config.ClientConfig;
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
    @ConditionalOnMissingBean(JrpcClient.class)
    public JrpcClient jrpcClient() {
        ClientConfig config = new ClientConfig();
        config.setZkHost(properties.getZkHost());
        config.setZkRoot(properties.getZkRoot());
        config.setPoolMinIdle(properties.getPoolMinIdle());
        config.setPoolMaxIdle(properties.getPoolMaxIdle());
        config.setPoolMaxTotal(properties.getPoolMaxTotal());
        return new JrpcClient(config);
    }
}
