package com.github.jingshouyan.jrpc.starter.desensitize;

import com.google.common.collect.Maps;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @author jingshouyan
 * #date 2018/10/26 11:35
 */
@Data
@ConfigurationProperties(prefix = "j-rpc.desensitize")
public class DesensitizeProperties {
    private Map<String,Integer> settings = Maps.newHashMap();
}
