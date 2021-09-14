package com.github.jingshouyan.jrpc.client.pool;

import com.github.jingshouyan.jrpc.base.info.ConnectInfo;
import com.github.jingshouyan.jrpc.client.config.ConnectConf;
import com.github.jingshouyan.jrpc.client.config.PoolConf;
import com.github.jingshouyan.jrpc.client.transport.Transport;
import lombok.SneakyThrows;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

import java.io.Closeable;

/**
 * @author jingshouyan
 * 2021-09-03 14:10
 **/
public class KeyedTransportPool implements Closeable {

    private final GenericKeyedObjectPool<ConnectInfo, Transport> innerPool;

    public static final int BORROW_TIMEOUT = 200;

    public KeyedTransportPool(PoolConf poolConf, ConnectConf connectConf){
        GenericKeyedObjectPoolConfig<Transport> config = new GenericKeyedObjectPoolConfig<>();
        config.setMinIdlePerKey(poolConf.getMinIdle());
        config.setMaxIdlePerKey(poolConf.getMaxIdle());
        config.setMaxTotalPerKey(poolConf.getMaxTotal());
        config.setTestOnCreate(poolConf.isTestOnCreate());
        config.setTestOnBorrow(poolConf.isTestOnBorrow());
        config.setTestOnReturn(poolConf.isTestOnReturn());
        config.setTestWhileIdle(poolConf.isTestWhileIdle());
        KeyedPooledObjectFactory<ConnectInfo, Transport> factory = new KeyedTransportFactory(connectConf);
        innerPool = new GenericKeyedObjectPool<>(factory, config);
    }

    @SneakyThrows
    public Transport borrowObject(ConnectInfo key) {

        return innerPool.borrowObject(key, BORROW_TIMEOUT);
    }

    public void returnObject(Transport transport) {
        if (null != transport) {
            innerPool.returnObject(transport.getKey(), transport);
        }

    }

    public void invalidateObject(Transport transport) throws Exception {
        if (null != transport) {
            innerPool.invalidateObject(transport.getKey(), transport);
        }
    }

    public void clear(ConnectInfo key) {

        innerPool.clear(key);
    }

    @Override
    public void close() {
        innerPool.close();
    }
}
