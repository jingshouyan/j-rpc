package com.jing.test.rpc;

import com.github.jingshouyan.jrpc.base.annotation.JrpcService;
import com.github.jingshouyan.jrpc.base.bean.InterfaceInfo;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import reactor.core.publisher.Mono;

/**
 * @author jingshouyan
 * 2021-09-15 07:44
 **/
@JrpcService(server = "test", version = "1.0")
public interface TestService {
    Mono<Rsp> traceTest(Token token, int i);

    Mono<InterfaceInfo> getServerInfo(Token token, Object obj);
}
