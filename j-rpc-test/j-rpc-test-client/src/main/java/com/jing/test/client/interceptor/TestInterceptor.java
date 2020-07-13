package com.jing.test.client.interceptor;

import com.github.jingshouyan.jrpc.base.action.ActionHandler;
import com.github.jingshouyan.jrpc.base.action.ActionInterceptor;
import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author jingshouyan
 * #date 2020/6/3 10:03
 */

public class TestInterceptor implements ActionInterceptor {

    @Override
    public Mono<Rsp> around(Token token, Req req, ActionHandler handler) {
        return null;
    }
}
