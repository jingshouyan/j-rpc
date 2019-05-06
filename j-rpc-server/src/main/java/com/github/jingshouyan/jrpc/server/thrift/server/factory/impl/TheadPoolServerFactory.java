package com.github.jingshouyan.jrpc.server.thrift.server.factory.impl;

import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.base.thrift.Jrpc;
import com.github.jingshouyan.jrpc.server.service.Rpc;
import com.github.jingshouyan.jrpc.server.thrift.server.factory.ServerFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;

/**
 * @author jingshouyan
 * 11/29/18 5:26 PM
 */
@Slf4j
public class TheadPoolServerFactory implements ServerFactory {

    @Override
    @SneakyThrows
    public TServer getServer(Rpc service, ServerInfo serverInfo) {
        int port = serverInfo.getPort();
        TNonblockingServerSocket serverSocket = new TNonblockingServerSocket(port);
        TProcessor tprocessor = new Jrpc.AsyncProcessor<>(service);
        TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverSocket);
        args.transportFactory(new TFramedTransport.Factory());
        args.protocolFactory(new TBinaryProtocol.Factory());
        args.processor(tprocessor);
        TThreadPoolServer server = new TThreadPoolServer(args);
        return server;
    }
}
