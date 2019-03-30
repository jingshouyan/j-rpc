package com.github.jingshouyan.jrpc.client.pool;

import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.base.thrift.Jrpc;
import com.github.jingshouyan.jrpc.client.transport.Transport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TNonblockingSocket;

import java.util.function.Supplier;


/**
 * @author jingshouyan
 * @date 2018/4/17 20:41
 */
@Slf4j
public class TransportFactory extends BasePooledObjectFactory<Transport> implements PooledObjectFactory<Transport> {

    private ServerInfo serverInfo;
    private static TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
    Supplier<TAsyncClientManager> supplier = () -> {
        try{
            return new TAsyncClientManager();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    };
    TAsyncClientManager clientManager = supplier.get();

    public TransportFactory(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    @Override
    public Transport create() {
        try {
            Transport transport = new Transport();
            transport.setKey(serverInfo.getInstance());

            TNonblockingSocket nonblockingSocket = new TNonblockingSocket(serverInfo.getHost(), serverInfo.getPort(),serverInfo.getTimeout());
//            TAsyncClientManager clientManager = new TAsyncClientManager();
            Jrpc.AsyncClient asyncClient = new Jrpc.AsyncClient(protocolFactory,clientManager,nonblockingSocket);
            transport.setNonblockingSocket(nonblockingSocket);
            transport.setAsyncClient(asyncClient);

            log.debug("client pool make object success. {}==>{},{}:{}",
                    serverInfo.getName(),serverInfo.getInstance(),serverInfo.getHost(),serverInfo.getPort());
            return transport;
        } catch (Exception e) {
            log.warn("client pool make object error.",e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public PooledObject<Transport> wrap(Transport transport) {
        return new DefaultPooledObject<>(transport);
    }

    @Override
    public void destroyObject(PooledObject<Transport> p){
        Transport transport = p.getObject();
        if(transport != null){
            transport.close();
        }
    }

    @Override
    public boolean validateObject(PooledObject<Transport> p) {
        Transport transport = p.getObject();
        return transport != null && transport.isOpen();
    }
}
