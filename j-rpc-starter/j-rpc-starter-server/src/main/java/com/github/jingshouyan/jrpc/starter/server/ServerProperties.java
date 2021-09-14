package com.github.jingshouyan.jrpc.starter.server;

import com.github.jingshouyan.jrpc.base.constant.BaseConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author jingshouyan
 * #date 2018/10/25 14:56
 */
@Data
@ConfigurationProperties(prefix = "j-rpc.server")
public class ServerProperties {

    private String name = "j-rpc";
    private String version = "1.0";
    private String host;
    private int port = 8888;
    private int maxReadBufferBytes = 25 * 1024 * 1024;
    private boolean register = true;
    private int selector = BaseConstant.CPU_NUM * 2;
    private int worker = BaseConstant.CPU_NUM * 4;
}
