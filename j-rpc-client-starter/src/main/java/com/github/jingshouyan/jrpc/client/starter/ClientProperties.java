package com.github.jingshouyan.jrpc.client.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author jingshouyan
 * #date 2018/10/26 11:35
 */
@Data
@ConfigurationProperties(prefix = "j-rpc.client")
public class ClientProperties {
    private String zkHost = "127.0.0.1:2181";
    private String zkRoot = "/com.github.jingshouyan.jrpc";;
    private int poolMinIdle = 10;
    private int poolMaxIdle = 50;
    private int poolMaxTotal = 200;
}
