package com.github.jingshouyan.jrpc.base.info;

import lombok.Data;

/**
 * thrift 服务配置信息
 * @author jingshouyan
 * 2021-09-06 18:10
 **/
@Data
public class ServiceInfo {
    private String ip;
    private int port;
    private String network;
    private String protocol;
    private int selectorThreads;
    private int workerThreads;
    private long maxReadBufferBytes;
}
