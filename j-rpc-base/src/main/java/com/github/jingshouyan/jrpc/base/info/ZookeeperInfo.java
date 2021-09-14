package com.github.jingshouyan.jrpc.base.info;

import com.google.common.base.Strings;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jingshouyan
 * 2021-09-06 11:13
 **/
@Data
@Slf4j
public class ZookeeperInfo {

    private String address;
    private String addressEnv;
    private String namespace = "/com.github.jingshouyan.jrpc";

    private int sessionTimeout = 20000;
    private int connectionTimeout = 5000;
    private int retryIntervalMs = 5000;

    public static final String DEFAULT_ADDRESS = "127.0.0.1:2181";

    public String addr() {
        if (!Strings.isNullOrEmpty(address)) {
            return address;
        }
        if (!Strings.isNullOrEmpty(addressEnv)) {
            String envAddress = System.getenv(addressEnv);
            if (!Strings.isNullOrEmpty(envAddress)) {
                return envAddress;
            }
        }
        log.warn("zookeeper address not config,use default[{}]", DEFAULT_ADDRESS);
        return DEFAULT_ADDRESS;
    }
}
