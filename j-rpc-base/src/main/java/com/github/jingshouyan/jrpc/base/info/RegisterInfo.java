package com.github.jingshouyan.jrpc.base.info;

import lombok.Data;

/**
 * 服务注册信息
 *
 * @author jingshouyan
 * 2021-07-09 09:04
 **/
@Data
public class RegisterInfo {
    private String name;
    private String version;
    private String ip;
    private int port;
    private String network;
    private String protocol;
    private int weight;
    private String startTime;
    private String ssid;
}
