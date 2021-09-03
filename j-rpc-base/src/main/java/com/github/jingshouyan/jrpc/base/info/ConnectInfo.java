package com.github.jingshouyan.jrpc.base.info;

import lombok.Data;

/**
 * 服务连接信息
 * @author jingshouyan
 * 2021-09-03 11:12
 **/
@Data
public class ConnectInfo {

    private String protocol;
    private String host;
    private int port;

    @Override
    public String toString() {
        return protocol + "://" + host + ":" + port;
    }
}
