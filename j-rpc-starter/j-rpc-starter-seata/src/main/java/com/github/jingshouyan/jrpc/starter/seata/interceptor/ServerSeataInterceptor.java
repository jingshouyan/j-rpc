package com.github.jingshouyan.jrpc.starter.seata.interceptor;

import com.github.jingshouyan.jrpc.base.action.ActionHandler;
import com.github.jingshouyan.jrpc.base.action.ActionInterceptor;
import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.starter.seata.SeataConstant;
import reactor.core.publisher.Mono;

/**
 * @author jingshouyan
 * #date 2019/9/25 19:26
 */

public class ServerSeataInterceptor implements SeataConstant, ActionInterceptor {

    @Override
    public Mono<Rsp> around(Token token, Req req, ActionHandler handler) {

        return null;
    }


}
