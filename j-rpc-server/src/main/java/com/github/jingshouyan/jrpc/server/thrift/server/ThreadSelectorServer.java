package com.github.jingshouyan.jrpc.server.thrift.server;

import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.base.thrift.Jrpc;
import com.github.jingshouyan.jrpc.server.service.Rpc;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;

/**
 * @author jingshouyan
 * #date 2018/10/24 23:53
 */
@Slf4j
public class ThreadSelectorServer implements Server{

    private static final String SELECTOR_THREADS = "thrift.selectorThreads";
    private static final String WORKER_THREADS = "thrift.workerThreads";

    @Override
    public TServer getServer(Rpc service, ServerInfo serverInfo) {
        int port = serverInfo.getPort();
        int cpuNum = Runtime.getRuntime().availableProcessors();
        int selectorThreads = cpuNum * 2;
        int workerThreads = cpuNum * 4;
        TServer server = null;
        try {
            log.debug("thrift service starting...[port:{}],async:[{}]", port, serverInfo.isAsync());
            TNonblockingServerSocket serverTransport = new TNonblockingServerSocket(port);
            //多线程半同步半异步
            TThreadedSelectorServer.Args tArgs = new TThreadedSelectorServer.Args(serverTransport);
            TProcessor tprocessor;
            if(serverInfo.isAsync()){
                tprocessor = new Jrpc.AsyncProcessor<>(service);
            } else{
                tprocessor = new Jrpc.Processor<>(service);
            }
            tArgs.processor(tprocessor);
            tArgs.transportFactory(new TFramedTransport.Factory());
            //设置读的最大参数块 默认最大long，容易引起内存溢出，必须限制
            tArgs.maxReadBufferBytes = serverInfo.getMaxReadBufferBytes();
            tArgs.selectorThreads(selectorThreads).workerThreads(workerThreads);
            //二进制协议
            tArgs.protocolFactory(new TBinaryProtocol.Factory());
            // 多线程半同步半异步的服务模型
            server = new TThreadedSelectorServer(tArgs);
            log.debug("{} = {}", SELECTOR_THREADS, selectorThreads);
            log.debug("{} = {}", WORKER_THREADS, workerThreads);
        } catch (Exception e) {
            log.error("thrift service start failed", e);
        }
        log.debug("thrift service started.  [port:{}]", port);
        return server;
    }


}

