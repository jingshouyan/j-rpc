package com.github.jingshouyan.jrpc.server.method.inner;

import com.github.jingshouyan.jrpc.base.bean.Empty;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.server.method.Method;

/**
 * @author jingshouyan
 * #date 2019/1/30 18:46
 */

public class Ping implements Method<Empty, Void> {

    @Override
    public Void action(Token token, Empty empty) {
        return null;
    }
}
