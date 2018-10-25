package com.github.jingshouyan.jrpc.base.bean;

import lombok.Data;

/**
 * @author jingshouyan
 * @date 2018/4/14 22:12
 */
@Data
public class ServerInfo {
    public ServerInfo(){}

    private String zkHost;
    private String name;
    private String version;
    private String host;
    private int port;
    private String startAt;
    private int timeout = 5000;
    private int maxReadBufferBytes = 25 * 1024 * 1024;
    private String updatedAt;
    private MonitorInfo monitorInfo;
    public String key(){
        return host+":"+port;
    }


    public void key(String key){
        String[] strings = key.split(":");
        host = strings[0];
        port = Integer.parseInt(strings[1]);
    }

    public void update(ServerInfo serverInfo){
        this.updatedAt = serverInfo.updatedAt;
        this.monitorInfo = serverInfo.monitorInfo;
    }
}
