package com.github.jingshouyan.jrpc.server.thrift.server.factory;

import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.server.service.Rpc;
import org.apache.thrift.server.TServer;

/**
 * @author jingshouyan
 * #date 2018/10/24 23:55
 */
public interface ServerFactory {
    /**
     * 获取TServer
     *
     * @param service    service实现
     * @param serverInfo 服务信息
     * @return TServer
     */
    TServer getServer(Rpc service, ServerInfo serverInfo);
}
