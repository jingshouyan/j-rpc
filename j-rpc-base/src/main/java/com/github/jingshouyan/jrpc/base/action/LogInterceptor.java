package com.github.jingshouyan.jrpc.base.action;

import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Router;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * @author jingshouyan
 * #date 2019/3/29 11:01
 */
@Slf4j
@AllArgsConstructor
public class LogInterceptor implements ActionInterceptor {

    private boolean server;

    private StringBuilder actionInfo(Req req) {
        StringBuilder sb = new StringBuilder();
        if (server) {
            sb.append("exec [")
                    .append(req.getMethod())
                    .append("] ");
        } else {
            sb.append("call [");
            Router router = req.getRouter();
            if (router != null) {
                sb.append(router.getServer()).append('.');
            }
            sb.append(req.getMethod())
                    .append("] ");
        }
        return sb;
    }

    @Override
    public Mono<Rsp> around(Token token, Req req, ActionHandler handler) {
        long start = System.currentTimeMillis();
        String actionInfo = actionInfo(req).toString();

        if (log.isDebugEnabled()) {
            log.debug("{} token: {}", actionInfo, token);
            log.debug("{} param: {}.", actionInfo, req.desensitizedParam());
        }
        return handler.handle(token, req).doOnSuccess(rsp -> {

            long end = System.currentTimeMillis();
            long cost = end - start;
            if (rsp.success()) {
                if (log.isDebugEnabled()) {
                    log.debug("{} end.use {}ms.code:{},message:{},data:{}",
                            actionInfo, cost, rsp.getCode(), rsp.getMessage(), rsp.desensitizedResult());
                }
            } else {
                if (log.isWarnEnabled()) {
                    log.warn("{} end.use {}ms.code:{},message:{},data:{}",
                            actionInfo, cost, rsp.getCode(), rsp.getMessage(), rsp.desensitizedResult());
                }
            }
        }).doOnError(e -> {
            long end = System.currentTimeMillis();
            long cost = end - start;
            log.error("{} use {}ms,error", actionInfo, cost, e);
        });
    }

    @Override
    public int order() {
        return Integer.MIN_VALUE + 10;
    }
}
