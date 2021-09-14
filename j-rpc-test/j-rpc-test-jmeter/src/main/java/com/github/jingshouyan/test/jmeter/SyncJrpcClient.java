package com.github.jingshouyan.test.jmeter;

import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.client.JrpcClient;
import com.github.jingshouyan.jrpc.client.Request;
import com.github.jingshouyan.jrpc.client.config.ClientConfig;
import com.github.jingshouyan.jrpc.registry.Discovery;
import com.github.jingshouyan.jrpc.registry.NodeManager;
import com.github.jingshouyan.jrpc.registry.zookeeper.ZkDiscovery;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryForever;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

public class SyncJrpcClient extends AbstractJrpcClient {

    private volatile static JrpcClient client;

    @Override
    public void setupTest(JavaSamplerContext context) {
        super.setupTest(context);
        synchronized (SyncJrpcClient.class) {
            if (client == null) {
                ClientConfig config = new ClientConfig();
                config.setZkHost(zkAddr);
                config.setPoolMaxIdle(100);
                config.setPoolMaxTotal(2000);
                config.setPoolMaxIdle(300);
                CuratorFramework c = CuratorFrameworkFactory
                        .builder().connectString(zkAddr).canBeReadOnly(true)
                        .connectionTimeoutMs(5000)
                        .sessionTimeoutMs(5000)
                        .retryPolicy(new RetryForever(5000))
                        .build();
                c.start();
                Discovery discovery = new ZkDiscovery(c,namespace);
                NodeManager nodeManager = new NodeManager(discovery);
                client = new JrpcClient(config,nodeManager);
            }
        }
    }

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        Token token = new Token();
        token.setUserId(userId);
        token.setTicket(ticket);
        SampleResult sr = new SampleResult();
        sr.setSamplerData(data);
        sr.sampleStart();
        Rsp rsp = Request.newInstance()
                .setClient(client)
                .setServer(server)
                .setVersion(version)
                .setMethod(method)
                .setParamJson(data)
                .setToken(token)
                .send();
        sr.setSuccessful(rsp.success());
        sr.setResponseData(rsp.json(), "utf-8");
        sr.sampleEnd();
        return sr;
    }
}
