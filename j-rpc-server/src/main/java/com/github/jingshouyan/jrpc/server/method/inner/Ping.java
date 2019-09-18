package com.github.jingshouyan.jrpc.server.method.inner;

import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.server.method.Method;

/**
 * @author jingshouyan
 * #date 2019/1/30 18:46
 */

public class Ping implements Method<Object, Object> {

    @Override
    public Object action(Token token, Object obj) {
        return obj;
    }
}
