package com.github.jingshouyan.test.jmeter;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;

public abstract class AbstractJrpcClient extends AbstractJavaSamplerClient {

    protected String zkAddr;
    protected String host;
    protected int port;
    protected String server;
    protected String method;
    protected String data;
    protected String userId;
    protected String ticket;

    @Override
    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();
        params.addArgument("zkAddr", "127.0.0.1:2181");
        params.addArgument("host", "127.0.0.1");
        params.addArgument("port", "0");
        params.addArgument("server", "");
        params.addArgument("method", "");
        params.addArgument("data", "");
        params.addArgument("userId", "");
        params.addArgument("ticket", "");
        return params;
    }

    @Override
    public void setupTest(JavaSamplerContext context) {
        zkAddr = context.getParameter("zkAddr");
        host = context.getParameter("host");
        port = Integer.valueOf(context.getParameter("port"));
        server = context.getParameter("server");
        method = context.getParameter("method");
        data = context.getParameter("data");
        userId = context.getParameter("userId");
        ticket = context.getParameter("ticket");
    }
}
