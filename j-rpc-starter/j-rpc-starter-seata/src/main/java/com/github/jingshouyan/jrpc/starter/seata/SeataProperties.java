package com.github.jingshouyan.jrpc.starter.seata;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author jingshouyan
 * #date 2019/9/25 17:06
 */
@Data
@ConfigurationProperties(prefix = "j-rpc.seata")
public class SeataProperties {
    private String applicationId = "jrpc";
    private String txServiceGroup = "jrpcTxGroup";
}
