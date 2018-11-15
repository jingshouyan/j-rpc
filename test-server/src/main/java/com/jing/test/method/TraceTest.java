package com.jing.test.method;

import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.client.JrpcClient;
import com.github.jingshouyan.jrpc.client.Request;
import com.github.jingshouyan.jrpc.client.starter.JrpcClientAutoConfiguration;
import com.github.jingshouyan.jrpc.server.method.Method;
import com.github.jingshouyan.jrpc.server.starter.ServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jingshouyan
 * 11/15/18 4:08 PM
 */
@Component
public class TraceTest implements Method<Integer,Integer> {

    @Autowired
    ServerProperties properties;

    @Autowired
    JrpcClient client;

    @Override
    public Integer action(Token token, Integer i) {
        if(i!=null && i>0){
            Request.newInstance().setClient(client)
                    .setServer(properties.getName())
                    .setMethod("traceTest")
                    .setParamObj(i-1)
                    .setOneway(true)
                    .send();
        }
        return i;
    }
}
