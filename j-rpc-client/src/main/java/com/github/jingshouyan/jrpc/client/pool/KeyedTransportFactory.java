package com.github.jingshouyan.jrpc.client.pool;

import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.base.exception.JrpcException;
import com.github.jingshouyan.jrpc.base.info.ConnectInfo;
import com.github.jingshouyan.jrpc.base.thrift.Jrpc;
import com.github.jingshouyan.jrpc.client.config.ConnectConf;
import com.github.jingshouyan.jrpc.client.transport.Transport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TNonblockingSocket;

import java.io.IOException;

/**
 * @author jingshouyan
 * 2021-09-03 14:22
 **/
@Slf4j
public class KeyedTransportFactory extends BaseKeyedPooledObjectFactory<ConnectInfo, Transport> {

    private final ConnectConf connectConf;
    private static final TProtocolFactory PROTOCOL_FACTORY = new TBinaryProtocol.Factory();
    private final TAsyncClientManager clientManager;

    public KeyedTransportFactory(ConnectConf connectConf) {
        try {
            this.connectConf = connectConf;
            // todo 多个后端是否使用不同的 manager?
            clientManager = new TAsyncClientManager();
        } catch (IOException e) {
            throw new JrpcException(Code.INIT_ERROR, e);
        }
    }

    @Override
    public Transport create(ConnectInfo key) throws Exception {
        try {
            Transport transport = new Transport();
            transport.setKey(key);
            TNonblockingSocket nonblockingSocket = new TNonblockingSocket(key.getHost(), key.getPort(), connectConf.getTimeout());
            Jrpc.AsyncClient asyncClient = new Jrpc.AsyncClient(PROTOCOL_FACTORY, clientManager, nonblockingSocket);
            asyncClient.setTimeout(connectConf.getTimeout());
            transport.setNonblockingSocket(nonblockingSocket);
            transport.setAsyncClient(asyncClient);
            log.debug("create transport success. {}", key);
            return transport;
        } catch (Exception e) {
            log.warn("create transport failed. {}", key);
            throw new RuntimeException(e);
        }
    }

    @Override
    public PooledObject<Transport> wrap(Transport value) {
        return new DefaultPooledObject<>(value);
    }

    @Override
    public void destroyObject(ConnectInfo key, PooledObject<Transport> p) {
        Transport transport = p.getObject();
        if (transport != null) {
            transport.close();
        }
    }

    @Override
    public boolean validateObject(ConnectInfo key, PooledObject<Transport> p) {
        Transport transport = p.getObject();
        return transport != null && transport.isOpen();
    }
}
