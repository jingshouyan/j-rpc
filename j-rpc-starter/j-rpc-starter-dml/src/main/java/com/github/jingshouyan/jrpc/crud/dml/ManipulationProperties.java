package com.github.jingshouyan.jrpc.crud.dml;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author jingshouyan
 * #date 2018/12/27 18:26
 */
@Data
@ConfigurationProperties(prefix = "j-rpc.server.plugin.crud")
public class ManipulationProperties {

    private String create = "*";
    private String update = "*";
    private String delete = "*";
}
