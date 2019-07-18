package com.github.jingshouyan.jrpc.base.action;

import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import reactor.core.publisher.Mono;

/**
 * @author jingshouyan
 * #date 2019/3/28 20:57
 */

public interface ActionInterceptor extends Comparable<ActionInterceptor> {

    /**
     * around handler
     *
     * @param token   token
     * @param req     请求
     * @param handler handler
     * @return mono Rsp
     */
    Mono<Rsp> around(Token token, Req req, ActionHandler handler);

    /**
     * order of interceptor
     *
     * @return order
     */
    default int order() {
        return 10;
    }

    /**
     * 比较
     *
     * @param that 另一个
     * @return int
     */
    @Override
    default int compareTo(ActionInterceptor that) {
        int a = this.order();
        int b = that.order();
        if (a == b) {
            return 0;
        } else if (a > b) {
            return 1;
        } else {
            return -1;
        }
    }
}
