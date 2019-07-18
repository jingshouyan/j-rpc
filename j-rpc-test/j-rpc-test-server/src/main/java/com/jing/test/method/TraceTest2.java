package com.jing.test.method;

import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.client.JrpcClient;
import com.github.jingshouyan.jrpc.client.Request;
import com.github.jingshouyan.jrpc.server.method.AsyncMethod;
import com.github.jingshouyan.jrpc.starter.server.ServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author jingshouyan
 * 11/15/18 4:08 PM
 */
@Component("traceTest2")
public class TraceTest2 implements AsyncMethod<Integer, Integer> {
    private static final int LOOP = 2;

    @Autowired
    ServerProperties properties;

    @Autowired
    JrpcClient client;

    @Override
    public Mono<Integer> action(Token token, Integer i) {
        return Mono.fromCallable(() -> {
            if (i != null && i > 0) {
                for (int j = 0; j < LOOP; j++) {
                    Request.newInstance().setClient(client)
                            .setServer(properties.getName())
                            .setMethod("traceTest2")
                            .setParamObj(i - 1)
                            .setOneway(true)
                            .asyncSend().subscribe();
                }
            }
            return i;
        });

    }
}
