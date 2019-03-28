package com.github.jingshouyan.jrpc.server.thrift.server;

import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.server.service.Rpc;
import org.apache.thrift.server.TServer;

/**
 * @author jingshouyan
 * #date 2018/10/24 23:55
 */
public interface Server {
    TServer getServer(Rpc service, ServerInfo serverInfo);
}
