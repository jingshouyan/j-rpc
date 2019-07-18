package com.github.jingshouyan.starter.forward.aop;

import com.github.jingshouyan.jrpc.base.action.ActionHandler;
import com.github.jingshouyan.jrpc.base.action.ActionInterceptor;
import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.client.JrpcClient;
import com.github.jingshouyan.jrpc.client.Request;
import com.github.jingshouyan.starter.forward.ForwardProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * @author jingshouyan
 * #date 2019/1/12 11:25
 */
@Slf4j
@AllArgsConstructor
public class JrpcForward implements ActionInterceptor {

    private JrpcClient client;
    private ForwardProperties properties;

    @Override
    public Mono<Rsp> around(Token token, Req req, ActionHandler handler) {
        if (properties.getMethods().containsKey(req.getMethod())) {
            String str = properties.getMethods().get(req.getMethod());
            String[] strings = str.split("\\.");
            String server = strings[0];
            String method = strings[1];
            log.debug("{} forward to {}.{}", req.getMethod(), server, method);
            return Request.newInstance()
                    .setClient(client)
                    .setServer(server)
                    .setMethod(method)
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
