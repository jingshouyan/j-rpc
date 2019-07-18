package com.github.jingshouyan.test.jmeter;

import com.github.jingshouyan.jrpc.base.thrift.Jrpc;
import com.github.jingshouyan.jrpc.base.thrift.ReqBean;
import com.github.jingshouyan.jrpc.base.thrift.RspBean;
import com.github.jingshouyan.jrpc.base.thrift.TokenBean;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.HashMap;

/**
 * @author jingshouyan
 * #date 2019/1/31 13:38
 */
@Slf4j
public class ClientUtil {
    @SneakyThrows
    public static Jrpc.Client client(String host, int port) {
        TSocket socket = new TSocket(host, port);
        socket.getSocket().setKeepAlive(true);
        socket.getSocket().setTcpNoDelay(true);
        socket.getSocket().setSoLinger(false, 0);
        socket.setTimeout(Integer.MAX_VALUE);
        TTransport tTransport = new TFramedTransport(socket, 25 * 1024 * 1024);
        tTransport.open();
        TProtocol tProtocol = new TBinaryProtocol(tTransport);
        return new Jrpc.Client(tProtocol);
    }

    @SneakyThrows
    public static RspBean call(Jrpc.Client client, String method, String data, TokenBean tokenBean) {
        tokenBean.setHeaders(new HashMap<>());
        ReqBean reqBean = new ReqBean();
        reqBean.setMethod(method);
        reqBean.setParam(data);
        return client.call(tokenBean, reqBean);
    }

}
