package com.github.jingshouyan.jrpc.base.action;

import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Token;

/**
 * @author jingshouyan
 * #date 2019/3/28 20:57
 */

public interface ActionInterceptor extends Comparable<ActionInterceptor>{

    ActionHandler around(Token token ,Req req ,ActionHandler handler);

    default int order() {
        return 10;
    }

    default int compareTo(ActionInterceptor that) {
        int a = this.order();
        int b = that.order();
        if(a == b){
            return 0;
        }else if(a > b) {
            return 1;
        }else {
            return -1;
        }
    }
}
