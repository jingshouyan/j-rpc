package com.github.jingshouyan.jrpc.starter.server;

import com.github.jingshouyan.jrpc.base.constant.BaseConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Locale;
import java.util.UUID;

/**
 * @author jingshouyan
 * #date 2018/10/25 14:56
 */
@Data
@ConfigurationProperties(prefix = "j-rpc.server")
public class ServerProperties {

    private String name = "j-rpc";
    private String version = "1.0";
    private String ssid = UUID.randomUUID().toString().toLowerCase(Locale.ROOT).replaceAll("-", "");
    private String network = "tcp";
    private String protocol = "thrift.binary";
    private int port = 8888;
    private int weight = 1;
    private int maxReadBuffer = 25 * 1024 * 1024;
    private boolean register = true;
    private int selector = BaseConstant.CPU_NUM;
    private int worker = BaseConstant.CPU_NUM * 4;
}
