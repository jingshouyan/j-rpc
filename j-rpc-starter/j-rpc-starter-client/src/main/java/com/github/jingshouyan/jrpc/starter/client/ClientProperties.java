package com.github.jingshouyan.jrpc.starter.client;

import com.github.jingshouyan.jrpc.client.config.ConnectConf;
import com.github.jingshouyan.jrpc.client.config.PoolConf;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author jingshouyan
 * #date 2018/10/26 11:35
 */
@Data
@ConfigurationProperties(prefix = "j-rpc.client")
public class ClientProperties {
    private PoolConf pool = new PoolConf();
    private ConnectConf connect = new ConnectConf();
}
