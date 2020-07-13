package com.github.jingshouyan.test.jmeter;

import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.client.JrpcClient;
import com.github.jingshouyan.jrpc.client.Request;
import com.github.jingshouyan.jrpc.client.config.ClientConfig;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

public class AsyncJrpcClient extends AbstractJrpcClient {

    private static JrpcClient client;

    @Override
    public void setupTest(JavaSamplerContext context) {
        super.setupTest(context);
        synchronized (AsyncJrpcClient.class) {
            if (client == null) {
                ClientConfig config = new ClientConfig();
                config.setZkHost(zkAddr);
                config.setPoolMaxIdle(100);
                config.setPoolMaxTotal(2000);
                config.setPoolMaxIdle(300);
                client = new JrpcClient(config);
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
