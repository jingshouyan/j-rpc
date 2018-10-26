package com.github.jingshouyan.jrpc.client.config;

import lombok.Data;

/**
 * @author jingshouyan
 * #date 2018/10/25 22:44
 */
@Data
public class ClientConfig {
    private String zkHost;
    private String zkRoot;
    private int poolMinIdle;
    private int poolMaxIdle;
    private int poolMaxTotal;
}
