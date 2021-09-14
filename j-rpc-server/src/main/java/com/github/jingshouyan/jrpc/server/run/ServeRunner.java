package com.github.jingshouyan.jrpc.server.run;

import com.github.jingshouyan.jrpc.base.info.ServiceInfo;
import com.github.jingshouyan.jrpc.server.service.Rpc;
import com.github.jingshouyan.jrpc.server.thrift.server.factory.util.ServerFactoryUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.server.TServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author jingshouyan
 * #date 2018/10/24 20:48
 */
@Slf4j
public class ServeRunner {
    private static final long UPDATE_DELAY = 600;

    private final ExecutorService SERVER_RUNNER_POOL = new ThreadPoolExecutor(1,
            1, 0L, TimeUnit.MICROSECONDS, new SynchronousQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("server-runner-%d").build(),
            new ThreadPoolExecutor.AbortPolicy()
    );

    private TServer tserver;

    private ServiceInfo serviceInfo;

    private Rpc rpc;


    public ServeRunner setServiceInfo(ServiceInfo serviceInfo) {
        if (this.serviceInfo == null) {
            log.debug("set server info : {}", serviceInfo);
            this.serviceInfo = serviceInfo;
        } else {
            log.warn("server info is already set.");
        }
        initServer();
        return this;
    }


    public ServeRunner setIface(Rpc rpc) {
        if (this.rpc == null) {
            log.debug("set server impl: {}", rpc);
            this.rpc = rpc;
        }
        initServer();
        return this;
    }

    private void initServer() {
        if (serviceInfo != null && rpc != null) {
            this.tserver = ServerFactoryUtil.getFactory().getServer(rpc, serviceInfo);
        }
    }


    public void start() {
        if (tserver.isServing()) {
            log.warn("service is already started, ignore");
            return;
        }
        SERVER_RUNNER_POOL.execute(() -> {
            try {
                tserver.serve();
            } catch (Exception e) {
                log.error("server start failed.", e);
                System.exit(-1);
            }
            log.debug("server stop...");
        });
    }

    public boolean isServing() {
        return tserver.isServing();
    }

    public void stop() {
        tserver.stop();
    }


}
