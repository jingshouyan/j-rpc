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

    private float rate = 0.1f;

    private String endpoint = "http://127.0.0.1:9411/api/v2/spans";

    private int dataShow = 0;

}
