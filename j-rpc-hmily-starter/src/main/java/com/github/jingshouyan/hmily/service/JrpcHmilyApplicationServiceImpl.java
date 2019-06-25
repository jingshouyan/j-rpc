package com.github.jingshouyan.hmily.service;

import org.apache.commons.lang3.RandomUtils;
import org.dromara.hmily.core.service.HmilyApplicationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author jingshouyan
 * #date 2018/12/24 17:49
 */
@Service("applicationService")
public class JrpcHmilyApplicationServiceImpl implements HmilyApplicationService {

    private static final String DEFAULT_APPLICATION_NAME = "hmilyJrpc";

    @Value("${spring.application.name:}")
    private String appName;

    @Value("${jrpc.server.name:}")
    private String jRpcName;

    @Override
    public String acquireName() {
        String tracingName = buildDefaultApplicationName();
        if (!StringUtils.isEmpty(appName)) {
            tracingName = appName;
        }
        if (!StringUtils.isEmpty(jRpcName)) {
            tracingName = jRpcName;
        }
        return tracingName;
    }

    private String buildDefaultApplicationName() {
        return DEFAULT_APPLICATION_NAME + RandomUtils.nextInt(1, 10);
    }
}
