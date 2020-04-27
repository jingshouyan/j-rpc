package com.jing.test;

import com.github.jingshouyan.jrpc.base.action.ActionHandler;
import com.github.jingshouyan.jrpc.base.action.ActionInterceptor;
import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * @author jingshouyan
 * #date 2019/10/21 19:45
 */
@Slf4j
public class InterceptorTest implements ActionInterceptor {

    private int order;

    public InterceptorTest(int order) {
        this.order = order;
    }

    @Override
    public Mono<Rsp> around(Token token, Req req, ActionHandler handler) {
        log.warn("test {} in", order);
        return handler.handle(token, req).doOnSuccess(rsp -> log.warn("test {} out", order));
    }

    @Override
    public int order() {
        return order;
    }
}
