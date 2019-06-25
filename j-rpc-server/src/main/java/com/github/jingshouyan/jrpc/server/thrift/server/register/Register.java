package com.github.jingshouyan.jrpc.server.thrift.server.register;

import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import org.apache.thrift.server.TServer;

/**
 * @author jingshouyan
 * #date 2018/10/25 14:06
 */
public interface Register {
    /**
     * register server
     *
     * @param server     TServer
     * @param serverInfo server information
     */
    void register(TServer server, ServerInfo serverInfo);

}
