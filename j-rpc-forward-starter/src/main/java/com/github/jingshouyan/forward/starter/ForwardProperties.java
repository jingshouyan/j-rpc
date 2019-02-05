package com.github.jingshouyan.forward.starter;

import com.google.common.collect.Maps;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @author jingshouyan
 * #date 2019/1/12 11:13
 */
@Data
@ConfigurationProperties(prefix = "j-rpc.forward")
public class ForwardProperties {
    private Map<String,String> methods = Maps.newHashMap();
}
