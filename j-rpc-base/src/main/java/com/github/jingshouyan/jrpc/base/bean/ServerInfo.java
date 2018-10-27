package com.github.jingshouyan.jrpc.base.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author jingshouyan
 * @date 2018/4/14 22:12
 */
@Data
public class ServerInfo {
    public ServerInfo(){}

    @JsonIgnore
    private String zkHost = "127.0.0.1:2181";
    @JsonIgnore
    private String zkRoot = "/com.github.jingshouyan.jrpc";

    private String name = "j-rpc";
    private String version = "v1.0";
    private String host = "127.0.0.1";
    private int port = 8888;
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
