package com.github.jingshouyan.forward.starter.aop;

import com.github.jingshouyan.forward.starter.ForwardProperties;
import com.github.jingshouyan.jrpc.base.action.ActionHandler;
import com.github.jingshouyan.jrpc.base.action.ActionInterceptor;
import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.client.JrpcClient;
import com.github.jingshouyan.jrpc.client.Request;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    public ActionHandler around(Token token, Req req, ActionHandler handler) {
        if(properties.getMethods().containsKey(req.getMethod())) {
            String str = properties.getMethods().get(req.getMethod());
            String[] strings = str.split("\\.");
            String server = strings[0];
            String method = strings[1];
            log.debug("{} forward to {}.{}",req.getMethod(),server,method);
            return (t,r) -> Request.newInstance()
                    .setClient(client)
                    .setServer(server)
                    .setMethod(method)
                    .setToken(t)
                    .setParamJson(r.getParam())
                    .setOneway(r.isOneway())
                    .asyncSend()
//                    .timeout(3, TimeUnit.SECONDS)
                    ;
        }
        return handler;
    }

    @Override
    public int order() {
        return Integer.MIN_VALUE + 1;
    }
}
