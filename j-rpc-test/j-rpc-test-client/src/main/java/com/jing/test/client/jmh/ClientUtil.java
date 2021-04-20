package com.jing.test.client.jmh;

import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.thrift.Jrpc;
import com.github.jingshouyan.jrpc.base.thrift.ReqBean;
import com.github.jingshouyan.jrpc.base.thrift.RspBean;
import com.github.jingshouyan.jrpc.base.thrift.TokenBean;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TNonblockingTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.layered.TFramedTransport;

import java.util.HashMap;
import java.util.function.Consumer;

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
    public static RspBean call(Jrpc.Client client, String method, Object param) {
        TokenBean tokenBean = new TokenBean();
        tokenBean.setHeaders(new HashMap<>());
        ReqBean reqBean = new ReqBean();
        reqBean.setMethod(method);
        reqBean.setParam(JsonUtil.toJsonString(param));
        return client.call(tokenBean, reqBean);
    }

    @SneakyThrows
    public static Jrpc.AsyncClient asyncClient(String host, int port) {
        TProtocolFactory protocol = new TBinaryProtocol.Factory();
        TAsyncClientManager clientManager = new TAsyncClientManager();
        TNonblockingTransport transport = new TNonblockingSocket(host, port, 5000);
        Jrpc.AsyncClient asyncClient = new Jrpc.AsyncClient(protocol, clientManager, transport);
        return asyncClient;
    }

    @SneakyThrows
    public static void asyncCall(Jrpc.AsyncClient client, String method, Object param, Consumer<Rsp> consumer) {
        TokenBean tokenBean = new TokenBean();
        tokenBean.setHeaders(new HashMap<>());
        ReqBean reqBean = new ReqBean();
        reqBean.setMethod(method);
        reqBean.setParam(JsonUtil.toJsonString(param));
        client.call(tokenBean, reqBean, new AsyncMethodCallback<RspBean>() {
            @Override
            public void onComplete(RspBean rspBean) {
                Rsp rsp = new Rsp(rspBean);
                consumer.accept(rsp);
            }

            @Override
            public void onError(Exception e) {
                log.error("error", e);
            }
        });
    }

}
