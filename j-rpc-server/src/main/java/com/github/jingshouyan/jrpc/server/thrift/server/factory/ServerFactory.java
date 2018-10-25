package com.github.jingshouyan.jrpc.server.thrift.server.factory;

import com.github.jingshouyan.jrpc.server.thrift.server.Server;
import com.github.jingshouyan.jrpc.server.thrift.server.ThreadSelectorServer;

/**
 * @author jingshouyan
 * #date 2018/10/24 23:57
 */
public class ServerFactory {

    public static Server getServer() {
        return new ThreadSelectorServer();
    }
}
