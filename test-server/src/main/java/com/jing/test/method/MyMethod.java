package com.jing.test.method;

import com.github.jingshouyan.jrpc.base.bean.Empty;
import com.github.jingshouyan.jrpc.server.method.Method;
import org.springframework.stereotype.Component;

/**
 * @author jingshouyan
 * #date 2018/10/26 11:57
 */
@Component
public class MyMethod implements Method<Empty,Void> {

    @Override
    public Void action(Empty empty) {
        return null;
    }


}
