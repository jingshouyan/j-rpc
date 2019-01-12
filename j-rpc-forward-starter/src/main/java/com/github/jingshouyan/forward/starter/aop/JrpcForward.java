package com.github.jingshouyan.forward.starter.aop;

import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.client.JrpcClient;
import com.github.jingshouyan.jrpc.client.Request;
import com.github.jingshouyan.forward.starter.ForwardProperties;
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
@Aspect
@AllArgsConstructor
public class JrpcForward {

    private JrpcClient client;
    private ForwardProperties properties;

    @Pointcut("bean(serverActionHandler) && execution(* *.handle(..))")
    public void aspect() {
    }


    @Around("aspect()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Token token = (Token)args[0];
        Req req = (Req) args[1];
        if(properties.getMethods().containsKey(req.getMethod())) {
            String[] strings = properties.getMethods().get(req.getMethod());
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
                    .send();
        }
        return joinPoint.proceed();
    }
}
