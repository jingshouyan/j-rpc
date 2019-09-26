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
 * #date 2019/9/26 11:13
 */

public class ClientSeatInterceptor implements ActionInterceptor {

    @Override
    public Mono<Rsp> around(Token token, Req req, ActionHandler handler) {
        String xid = RootContext.getXID();
        if (xid != null) {
            token.set(RootContext.KEY_XID, xid);
        }
        return handler.handle(token, req);
    }
}
