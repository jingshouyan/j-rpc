package com.github.jingshouyan.jrpc.base.action;

import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import io.reactivex.Single;

/**
 * @author jingshouyan
 * #date 2019/3/28 20:57
 */

public interface ActionInterceptor extends Comparable<ActionInterceptor>{

    ActionHandler around(Token token ,Req req ,ActionHandler handler);

    default int order() {
        return 0;
    }

    default int compareTo(ActionInterceptor that) {
        return this.order() - that.order();
    }
}
