package com.github.jingshouyan.starter.forward.aop;

import com.github.jingshouyan.jrpc.base.action.ActionHandler;
import com.github.jingshouyan.jrpc.base.action.ActionInterceptor;
import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.client.JrpcClient;
import com.github.jingshouyan.jrpc.client.Request;
import com.github.jingshouyan.starter.forward.ForwardProperties;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author jingshouyan
 * #date 2019/1/12 11:25
 */
@Slf4j
public class JrpcForward implements ActionInterceptor {

    private static final char POINT = '.';
    private static final char AT = '@';

    private final JrpcClient client;

    private final Map<String, Forward> forwardMap;

    public JrpcForward(JrpcClient client, ForwardProperties properties, String defaultVersion) {
        this.client = client;
        forwardMap = Maps.newHashMapWithExpectedSize(properties.getMethods().size());
        for (Map.Entry<String, String> entry : properties.getMethods().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            int idx = value.indexOf(POINT);
            if (idx > 0) {
                String service = value.substring(0, idx);
                String method = value.substring(idx);
                String version = defaultVersion;
                idx = service.indexOf(AT);
                if (idx > 0) {
                    version = service.substring(idx);
                    service = service.substring(0, idx);
                }
                Forward f = new Forward(service, version, method);
                forwardMap.put(key, f);
                log.info("{} forward to {}", key, f);
            } else {
                log.warn("{} ==> {}, wrong format",key,value);
            }
        }
    }

    @Override
    public Mono<Rsp> around(Token token, Req req, ActionHandler handler) {
        Forward f = forwardMap.get(req.getMethod());
        if (f != null) {
            log.debug("{} forward to {}", req.getMethod(), f);
            return Request.newInstance()
                    .setClient(client)
                    .setServer(f.service)
                    .setVersion(f.version)
                    .setMethod(f.method)
                    .setToken(token)
                    .setParamJson(req.getParam())
                    .setOneway(req.isOneway())
                    .asyncSend();
        }
        return handler.handle(token, req);
    }

    @Override
    public int order() {
        return Integer.MIN_VALUE + 1;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Forward {
        private String service;
        private String version;
        private String method;

        @Override
        public String toString() {
            return service + AT + version + POINT + method;
        }
    }
}
