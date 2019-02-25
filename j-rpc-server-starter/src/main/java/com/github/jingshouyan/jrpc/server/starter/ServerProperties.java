package com.github.jingshouyan.jrpc.server.starter;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author jingshouyan
 * #date 2018/10/25 14:56
 */
@Data
@ConfigurationProperties(prefix = "j-rpc.server")
public class ServerProperties {

    private String zkHost = "127.0.0.1:2181";
    private String zkRoot = "/com.github.jingshouyan.jrpc";
    private String name = "j-rpc";
    private String version = "1.0";
    private String host;
    private int port = 8888;
    private int timeout = 5000;
    private int maxReadBufferBytes = 25 * 1024 * 1024;
    private boolean register = true;
    private boolean async = false;
}
