package com.jing.test.client.rpc;

import com.github.jingshouyan.jrpc.base.annotation.JrpcService;
import com.github.jingshouyan.jrpc.base.bean.InterfaceInfo;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import reactor.core.publisher.Mono;

import java.util.List;

@JrpcService(server = "test")
public interface TestService {

    void TraceTest2(Token token, int i);
    Rsp asyncErr(Token token, String abc);
    String asyncTest(Token token, String abc);
    Mono<Object> myMethod(Token token, Object abc);
    Mono<Object> testMethod(Token token, List<String> abc);
    Mono<Rsp> traceTest(Token token, int i);
    Mono<InterfaceInfo> getServerInfo(Token token, Object obj);
}
