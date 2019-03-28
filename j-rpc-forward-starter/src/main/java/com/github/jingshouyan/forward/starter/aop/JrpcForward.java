package com.github.jingshouyan.forward.starter.aop;

import com.github.jingshouyan.forward.starter.ForwardProperties;
import com.github.jingshouyan.jrpc.base.action.ActionInterceptor;
import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.client.JrpcClient;
import com.github.jingshouyan.jrpc.client.Request;
import io.reactivex.Single;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

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
    public Single<Rsp> around(Token token, Req req, Single<Rsp> single) {
        if(properties.getMethods().containsKey(req.getMethod())) {
            String str = properties.getMethods().get(req.getMethod());
            String[] strings = str.split("\\.");
            String server = strings[0];
            String method = strings[1];
            log.debug("{} forward to {}.{}",req.getMethod(),server,method);
            return Request.newInstance()
                    .setClient(client)
                    .setServer(server)
                    .setMethod(method)
                    .setToken(token)
                    .setParamJson(req.getParam())
                    .setOneway(req.isOneway())
                    .asyncSend();
        }
        return single;
    }
}
