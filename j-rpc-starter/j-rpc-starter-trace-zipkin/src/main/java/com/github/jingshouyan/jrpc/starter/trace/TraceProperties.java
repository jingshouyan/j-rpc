package com.github.jingshouyan.jrpc.starter.trace;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author jingshouyan
 * #date 2018/11/2 17:47
 */
@Data
@ConfigurationProperties(prefix = "j-rpc.trace")
public class TraceProperties {

    private String name;

    private String endpoint = "http://127.0.0.1:9411/api/v2/spans";

    private float rate = 0.1f;

    /**
     * 是否使用 TransmittableThreadLocal 存储 TraceContext
     * 会有些性能影响
     */
    private boolean ttl = false;

    /**
     * 请求 入参,返回 是否展示
     * 0: 不展示
     * 1: 失败时展示
     * 2: 展示
     */
    private int dataShow = 0;

}
