package com.github.jingshouyan.jrpc.client.config;

import com.github.jingshouyan.jrpc.base.constant.BaseConstant;
import lombok.Data;

/**
 * @author jingshouyan
 * #date 2018/10/25 22:44
 */
@Data
public class ClientConfig {
    private String zkHost = "127.0.0.1:2181";
    private String zkRoot = "/com.github.jingshouyan.jrpc";
    private int poolMinIdle = 10;
    private int poolMaxIdle = 200;
    private int poolMaxTotal = 500;

    private int callbackThreadPoolSize = BaseConstant.CPU_NUM * 2;
}
