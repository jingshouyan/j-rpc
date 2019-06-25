package com.github.jingshouyan.jrpc.client.transport;

import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.client.pool.TransportPool;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.Map;

/**
 * @author jingshouyan
 * @date 2018/4/18 11:22
 */
@Slf4j
public class TransportProvider {
    private static final Map<String, TransportPool> TRANSPORT_POOL_MAP = Maps.newConcurrentMap();

    private GenericObjectPoolConfig cfg;

    public TransportProvider(GenericObjectPoolConfig cfg) {
        this.cfg = cfg;
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.debug("client stop. clean transport pool");
            TRANSPORT_POOL_MAP.forEach((key, transport) -> {
                transport.close();
            });
        }));
    }

    public Transport get(ServerInfo serverInfo) throws Exception {
        return TRANSPORT_POOL_MAP.computeIfAbsent(serverInfo.getInstance(), key -> new TransportPool(serverInfo, cfg)).get();
    }

    public void restore(Transport transport) {
        if (null == transport) {
            return;
        }
        TransportPool transportPool = TRANSPORT_POOL_MAP.get(transport.getKey());
        if (null != transportPool) {
            transportPool.restore(transport);
        }
    }

    public void invalid(Transport transport) {
        if (null == transport) {
            return;
        }
        try {
            TransportPool transportPool = TRANSPORT_POOL_MAP.get(transport.getKey());
            if (null != transportPool) {
                transportPool.invalid(transport);
            }
        } catch (Exception e) {
            log.error("pool invalid object error", e);
        }
    }

    public void close(ServerInfo serverInfo) {
        TransportPool transportPool = TRANSPORT_POOL_MAP.remove(serverInfo.getInstance());
        if (null != transportPool) {
            transportPool.close();
        }
    }

    public void loseSingle(ServerInfo serverInfo) {
        try {
            TransportPool pool = TRANSPORT_POOL_MAP.get(serverInfo.getInstance());
            if (pool != null) {
                Transport transport = pool.get();
                boolean open = transport.isOpen();
                if (!open) {
                    close(serverInfo);
                } else {
                    pool.restore(transport);
                }
            }
        } catch (Exception e) {
            close(serverInfo);
        }

    }


}
