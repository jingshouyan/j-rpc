package com.github.jingshouyan.jrpc.starter.registry;

import com.github.jingshouyan.jrpc.base.info.ZookeeperInfo;
import com.github.jingshouyan.jrpc.registry.Discovery;
import com.github.jingshouyan.jrpc.registry.NodeManager;
import com.github.jingshouyan.jrpc.registry.Registry;
import com.github.jingshouyan.jrpc.registry.zookeeper.ZkDiscovery;
import com.github.jingshouyan.jrpc.registry.zookeeper.ZkRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryForever;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author jingshouyan
 * 2021-09-06 10:57
 **/
@Slf4j
@Configuration
@EnableConfigurationProperties(RegistryProperties.class)
public class JrpcRegistryAutoConfiguration {

    @Resource
    private RegistryProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public CuratorFramework curatorFramework() {
        ZookeeperInfo zookeeperInfo = properties.getZookeeper();
        CuratorFramework client = CuratorFrameworkFactory
                .builder().connectString(zookeeperInfo.addr()).canBeReadOnly(true)
                .connectionTimeoutMs(zookeeperInfo.getConnectionTimeout())
                .sessionTimeoutMs(zookeeperInfo.getSessionTimeout())
                .retryPolicy(new RetryForever(zookeeperInfo.getRetryIntervalMs()))
                .build();
        client.start();
        return client;
    }

    @Bean
    @ConditionalOnMissingBean
    public Registry registry(CuratorFramework curatorFramework) {
        ZookeeperInfo zookeeperInfo = properties.getZookeeper();
        return new ZkRegistry(curatorFramework, zookeeperInfo.getNamespace());
    }

    @Bean
    @ConditionalOnMissingBean
    public Discovery discovery(CuratorFramework curatorFramework) {
        ZookeeperInfo zookeeperInfo = properties.getZookeeper();
        return new ZkDiscovery(curatorFramework, zookeeperInfo.getNamespace());
    }

    @Bean
    @ConditionalOnMissingBean
    public NodeManager nodeManager(Discovery discovery) {
        return new NodeManager(discovery);
    }

}
