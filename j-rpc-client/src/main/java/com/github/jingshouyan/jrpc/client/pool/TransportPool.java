package com.github.jingshouyan.jrpc.client.pool;

import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.client.transport.Transport;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.async.TAsyncClientManager;

import java.io.Closeable;

/**
 * @author jingshouyan
 * @date 2018/4/17 21:39
 */
public class TransportPool implements Closeable {
    private GenericObjectPool<Transport> innerPool;
    @Getter
    private ServerInfo serverInfo;

    private TAsyncClientManager clientManager;

    private static final int BORROW_TIMEOUT = 3000;

    @SneakyThrows
    public TransportPool(ServerInfo serverInfo, GenericObjectPoolConfig conf) {
        this.serverInfo = serverInfo;
        clientManager = new TAsyncClientManager();
        innerPool = new GenericObjectPool<>(new TransportFactory(serverInfo, clientManager), conf);
    }

    /**
     * 取
     *
     * @return TTransport
     * @throws Exception
     */
    public Transport get() throws Exception {
        return innerPool.borrowObject(BORROW_TIMEOUT);
    }

    /**
     * 还
     *
     * @param transport Transport
     */
    public void restore(Transport transport) {
        innerPool.returnObject(transport);
    }

    /**
     * 失效
     *
     * @param transport TTransport
     * @throws Exception
     */
    public void invalid(Transport transport) throws Exception {
        innerPool.invalidateObject(transport);
    }

    @Override
    public void close() {
        clientManager.stop();
        innerPool.close();
    }
}
