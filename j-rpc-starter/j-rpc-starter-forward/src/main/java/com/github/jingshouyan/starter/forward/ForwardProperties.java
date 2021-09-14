package com.github.jingshouyan.starter.forward;

import com.github.jingshouyan.jrpc.base.info.ForwardInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * @author jingshouyan
 * #date 2019/1/12 11:13
 */
@Data
@ConfigurationProperties(prefix = "j-rpc.forward")
public class ForwardProperties {
    private List<ForwardInfo> methods = Lists.newArrayList();
}
