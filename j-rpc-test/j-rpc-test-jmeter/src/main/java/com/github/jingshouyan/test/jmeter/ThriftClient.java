package com.github.jingshouyan.test.jmeter;

import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.thrift.Jrpc;
import com.github.jingshouyan.jrpc.base.thrift.RspBean;
import com.github.jingshouyan.jrpc.base.thrift.TokenBean;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

public class ThriftClient extends AbstractJrpcClient {

    private Jrpc.Client client;

    @Override
    public void setupTest(JavaSamplerContext context) {
        super.setupTest(context);
        client = ClientUtil.client(host, port);
    }

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        TokenBean tokenBean = new TokenBean();
        tokenBean.setUserId(userId).setTicket(ticket);
        SampleResult sr = new SampleResult();
        sr.setSamplerData(data);
        sr.sampleStart();
        try {
            RspBean rspBean = ClientUtil.call(client, method, data, tokenBean);
            Rsp rsp = new Rsp(rspBean);
            sr.setResponseData(rsp.json(), "utf-8");
            sr.setSuccessful(rsp.success());
        } catch (Throwable e) {
            sr.setResponseData(e.getMessage(), "utf-8");
            sr.setSuccessful(false);
        } finally {
            sr.sampleEnd();
        }
        return sr;
    }
}
