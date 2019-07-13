package com.github.jingshouyan.jrpc.client.node;

import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.client.pool.TransportPool;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jingshouyan
 * #date 2018/4/18 0:39
 */
@Slf4j
public class Node implements Closeable {

    @Getter
    private ServerInfo serverInfo;
    private GenericObjectPoolConfig poolConfig;
    @Getter
    private AtomicInteger count = new AtomicInteger(0);
    @Getter
    @Setter
    private boolean health = true;
    private volatile TransportPool pool;

    public Node(ServerInfo serverInfo, GenericObjectPoolConfig poolConfig) {
        this.serverInfo = serverInfo;
        this.poolConfig = poolConfig;
    }

    public TransportPool pool() {
        TransportPool tmp = pool;
        if (null == tmp) {
            synchronized (this) {
                tmp = pool;
                if (null == tmp) {
                    pool = new TransportPool(serverInfo, poolConfig);
                }
            }
        }
        return pool;
    }

    @Override
    public void close() {
        log.debug("node[{} : {}:{}] close", serverInfo.getName(), serverInfo.getHost(), serverInfo.getPort());
        if (pool != null) {
            pool.close();
        }
    }

}
