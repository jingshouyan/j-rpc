package com.github.jingshouyan.jrpc.server.thrift.server.factory.util;

import com.github.jingshouyan.jrpc.server.thrift.server.factory.ServerFactory;
import com.github.jingshouyan.jrpc.server.thrift.server.factory.impl.ThreadSelectorServerFactory;

/**
 * @author jingshouyan
 * #date 2018/10/24 23:57
 */
public class ServerFactoryUtil {

    public static ServerFactory getFactory() {
        return new ThreadSelectorServerFactory();
    }
}
