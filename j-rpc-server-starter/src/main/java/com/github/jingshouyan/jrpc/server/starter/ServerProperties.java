package com.github.jingshouyan.jrpc.server.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author jingshouyan
 * #date 2018/10/25 14:56
 */
@Data
@ConfigurationProperties(prefix = "j-rpc")
public class ServerProperties {

    private String zkHost = "127.0.0.1:2181";
    private String zkRoot = "/com.github.jingshouyan.jrpc";

    private String name = "j-rpc";
    private String version = "v1.0";
    private String host = "127.0.0.1";
    private int port = 8888;
    private String startAt;
    private int timeout = 5000;
    private int maxReadBufferBytes = 25 * 1024 * 1024;
    private String updatedAt;


    private String logRootPath = "";
    private String logLevel = "DEBUG";
    private String logRef="STDOUT";
}
