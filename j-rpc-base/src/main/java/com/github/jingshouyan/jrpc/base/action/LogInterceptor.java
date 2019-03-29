package com.github.jingshouyan.jrpc.base.action;

import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Router;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import io.reactivex.Single;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
        if(server){
            sb.append("action [")
                    .append(req.getMethod())
                    .append("] ");
        } else {
            sb.append("call [");
            Router router = req.getRouter();
            if(router != null) {
                sb.append(router.getServer()).append('.');
            }
            sb.append(req.getMethod())
                    .append("] ");
        }
        return sb;
    }

    @Override
    public ActionHandler around(Token token, Req req, ActionHandler handler) {
        return (t,r) -> {
            long start = System.nanoTime();
            String actionInfo = actionInfo(req).toString();
            log.debug("{} token: {}",actionInfo,token);
            log.debug("{} param: {}.",actionInfo,req.getParam());
            Single<Rsp> single = handler.handle(t, r).doOnSuccess(rsp -> {
                long end = System.nanoTime();
                log.debug("{} end. rsp: {}", actionInfo, rsp.json());
                log.debug("{} use {} ns", actionInfo, end - start);
            });
            return single;
        };
    }

    @Override
    public int order() {
        return Integer.MIN_VALUE;
    }
}
