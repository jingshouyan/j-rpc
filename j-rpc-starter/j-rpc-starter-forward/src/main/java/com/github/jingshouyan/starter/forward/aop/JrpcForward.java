package com.github.jingshouyan.starter.forward.aop;

import com.github.jingshouyan.jrpc.base.action.ActionHandler;
import com.github.jingshouyan.jrpc.base.action.ActionInterceptor;
import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.base.info.ForwardInfo;
import com.github.jingshouyan.jrpc.client.JrpcClient;
import com.github.jingshouyan.jrpc.client.Request;
import com.github.jingshouyan.starter.forward.ForwardProperties;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

/**
 * @author jingshouyan
 * #date 2019/1/12 11:25
 */
@Slf4j
public class JrpcForward implements ActionInterceptor {


    private final JrpcClient client;

    private final Map<String, ForwardInfo> forwardMap;

    public JrpcForward(JrpcClient client, ForwardProperties properties, String defaultVersion) {
        this.client = client;
        forwardMap = Maps.newHashMapWithExpectedSize(properties.getMethods().size());
        for (ForwardInfo f : properties.getMethods()) {
            if (valid(f)) {
                if (Objects.isNull(f.getVersion())) {
                    f.setVersion(defaultVersion);
                }
                forwardMap.put(f.getOrigin(), f);
                log.info("add forward {}", f);
            } else {
                log.warn("{}, illegal param.", f);
            }
        }
    }

    private boolean valid(ForwardInfo f) {
        return Objects.nonNull(f.getOrigin())
                && Objects.nonNull(f.getService())
                && Objects.nonNull(f.getMethod());
    }

    @Override
    public Mono<Rsp> around(Token token, Req req, ActionHandler handler) {
        ForwardInfo f = forwardMap.get(req.getMethod());
        if (f != null) {
            log.debug("{} forward to {}", req.getMethod(), f);
            return Request.newInstance()
                    .setClient(client)
                    .setServer(f.getService())
                    .setVersion(f.getVersion())
                    .setMethod(f.getMethod())
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

}
