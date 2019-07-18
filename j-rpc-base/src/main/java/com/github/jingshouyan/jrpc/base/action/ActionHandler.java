package com.github.jingshouyan.jrpc.base.action;

import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import reactor.core.publisher.Mono;

/**
 * @author jingshouyan
 * #date 2018/11/2 22:24
 */
public interface ActionHandler {
    /**
     * 执行请求
     *
     * @param token token
     * @param req   req
     * @return rsp
     */
    Mono<Rsp> handle(Token token, Req req);
}
