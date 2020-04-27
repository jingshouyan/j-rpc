package com.github.jingshouyan.jrpc.starter.seata.interceptor;

import com.github.jingshouyan.jrpc.base.action.ActionHandler;
import com.github.jingshouyan.jrpc.base.action.ActionInterceptor;
import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import io.seata.core.context.RootContext;
import reactor.core.publisher.Mono;

/**
 * @author jingshouyan
 * #date 2019/9/25 19:26
 */

public class ServerSeataInterceptor implements ActionInterceptor {

    @Override
    public Mono<Rsp> around(Token token, Req req, ActionHandler handler) {
        String rpcXid = token.get(RootContext.KEY_XID);
        if (rpcXid != null) {
            RootContext.bind(rpcXid);
        }
        return handler.handle(token, req).doOnNext(rsp -> RootContext.unbind());
    }


}
