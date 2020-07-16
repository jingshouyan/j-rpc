package com.jing.test.method;

import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.client.JrpcClient;
import com.github.jingshouyan.jrpc.client.Request;
import com.github.jingshouyan.jrpc.server.method.AsyncMethod;
import com.github.jingshouyan.jrpc.starter.server.ServerProperties;
import com.jing.test.rpc.TestService;
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

    @Autowired
    TestService testService;

    @Override
    public Mono<Integer> action(Token token, Integer i) {
        if (i <= 0) {
            return Mono.just(i);
        }
        testService.traceTest2(token, i - 1).subscribe();
        return testService.traceTest2(token, i - 1);
    }
}
