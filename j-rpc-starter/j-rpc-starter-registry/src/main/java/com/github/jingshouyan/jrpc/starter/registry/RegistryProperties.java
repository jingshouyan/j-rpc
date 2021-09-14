package com.github.jingshouyan.jrpc.starter.registry;

import com.github.jingshouyan.jrpc.base.info.ZookeeperInfo;
import com.google.common.base.Strings;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author jingshouyan
 * 2021-09-06 11:03
 **/
@Data
@Slf4j
@ConfigurationProperties(prefix = "j-rpc.registry")
public class RegistryProperties {
    public static final String DEFAULT_IP = "127.0.0.1";

    private String model;
    private String inet = "";
    private String inetEnv = "";
    private ZookeeperInfo zookeeper = new ZookeeperInfo();


    public String localIp() {
        if (!Strings.isNullOrEmpty(inet)) {
            return inet;
        }
        if (!Strings.isNullOrEmpty(inetEnv)) {
            String envIp = System.getenv(inetEnv);
            if (!Strings.isNullOrEmpty(envIp)) {
                return envIp;
            }
        }
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("获取本机ip失败,使用{}", DEFAULT_IP, e);
            return DEFAULT_IP;
        }
    }
}
