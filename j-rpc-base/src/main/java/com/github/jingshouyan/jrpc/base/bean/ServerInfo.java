package com.github.jingshouyan.jrpc.base.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.jingshouyan.jrpc.base.constant.BaseConstant;
import lombok.Data;

import java.util.UUID;

/**
 * @author jingshouyan
 * #date 2018/4/14 22:12
 */
@Data
public class ServerInfo {
    public ServerInfo() {
        instance = UUID.randomUUID().toString().toLowerCase();
    }


    @JsonIgnore
    private String zkHost = "127.0.0.1:2181";
    @JsonIgnore
    private String zkRoot = "/com.github.jingshouyan.jrpc";

    private String name = "j-rpc";
    private String version = "1.0";
    private String host = "127.0.0.1";
    private int port = 8888;
    private String startAt;
    private int timeout = 60000;
    private int maxReadBufferBytes = 25 * 1024 * 1024;
    private String updatedAt;
    private MonitorInfo monitorInfo;
    private String instance;
    private int selector = BaseConstant.CPU_NUM;
    private int worker = BaseConstant.CPU_NUM * 4;


    public void update(ServerInfo serverInfo) {
        this.updatedAt = serverInfo.updatedAt;
        this.monitorInfo = serverInfo.monitorInfo;
    }
}
