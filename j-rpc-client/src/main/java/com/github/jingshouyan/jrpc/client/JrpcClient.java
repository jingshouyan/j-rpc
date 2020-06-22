package com.github.jingshouyan.jrpc.client;

import com.github.jingshouyan.jrpc.base.action.ActionHandler;
import com.github.jingshouyan.jrpc.base.action.ActionInterceptor;
import com.github.jingshouyan.jrpc.base.action.ActionInterceptorHolder;
import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.base.exception.JrpcException;
import com.github.jingshouyan.jrpc.base.thrift.Jrpc;
import com.github.jingshouyan.jrpc.base.thrift.ReqBean;
import com.github.jingshouyan.jrpc.base.thrift.RspBean;
import com.github.jingshouyan.jrpc.base.thrift.TokenBean;
import com.github.jingshouyan.jrpc.base.util.rsp.RspUtil;
import com.github.jingshouyan.jrpc.client.config.ClientConfig;
import com.github.jingshouyan.jrpc.client.discover.ZkDiscover;
import com.github.jingshouyan.jrpc.client.node.Node;
import com.github.jingshouyan.jrpc.client.pool.TransportPool;
import com.github.jingshouyan.jrpc.client.transport.Transport;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.async.AsyncMethodCallback;
import org.slf4j.MDC;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * @author jingshouyan
 * #date 2018/10/26 9:42
 */
@Slf4j
public class JrpcClient implements ActionHandler {

    private ClientConfig config;
    private ZkDiscover zkDiscover;

    public JrpcClient(ClientConfig config) {
        this.config = config;
        this.zkDiscover = new ZkDiscover(config);
    }

    public Map<String, List<ServerInfo>> serverMap() {
        Map<String, List<Node>> nodeMap = zkDiscover.nodeMap();
        Map<String, List<ServerInfo>> map = new HashMap<>(nodeMap.size());
        for (Map.Entry<String, List<Node>> entry : nodeMap.entrySet()) {
            String key = entry.getKey();
            List<ServerInfo> value = entry.getValue().stream()
                    .map(Node::getServerInfo).collect(Collectors.toList());
            map.put(key, value);
        }
        return map;
    }

    @Override
    public Mono<Rsp> handle(Token token, Req req) {
        ActionHandler handler = this::call;
        for (ActionInterceptor interceptor : ActionInterceptorHolder.getClientInterceptors()) {
            final ActionHandler ah = handler;
            handler = (t, r) -> interceptor.around(t, r, ah);
        }
        Mono<Rsp> mono = handler.handle(token, req);
        return mono;
    }

    private Mono<Rsp> call(Token token, Req req) {
        Mono<Rsp> mono = Mono.create(monoSink -> {
            TransportPool pool = null;
            Transport transport = null;
            try {
                Node node = zkDiscover.getNode(req.getRouter());
                pool = node.pool();
                transport = pool.get();
                TransportPool poolTmp = pool;
                Transport transportTmp = transport;
                Jrpc.AsyncClient client = transport.getAsyncClient();
                TokenBean tokenBean = token.tokenBean();
                ReqBean reqBean = req.reqBean();
                if (req.isOneway()) {
                    client.send(tokenBean, reqBean, new AsyncMethodCallback<Void>() {
                        @Override
                        public void onComplete(Void aVoid) {
                            restore(poolTmp, transportTmp);
                            monoSink.success(RspUtil.success());
                        }

                        @Override
                        public void onError(Exception e) {
                            invalid(poolTmp, transportTmp);
                            monoSink.error(e);
                        }
                    });
                } else {
                    client.call(tokenBean, reqBean, new AsyncMethodCallback<RspBean>() {
                        @Override
                        public void onComplete(RspBean rspBean) {
                            restore(poolTmp, transportTmp);
                            monoSink.success(new Rsp(rspBean));
                        }

                        @Override
                        public void onError(Exception e) {
                            invalid(poolTmp, transportTmp);
                            monoSink.error(e);
                        }
                    });
                }
            } catch (Throwable e) {
                log.error("call rpc error.", e);
                invalid(pool, transport);
                monoSink.error(e);
            }
        });
        String traceId = MDC.get("traceId");
        String spanId = MDC.get("spanId");
        String parentId = MDC.get("parentId");
        mono = mono.doOnEach(signal -> {
            if (signal.getType() == SignalType.ON_NEXT
                    || signal.getType() == SignalType.ON_ERROR) {
                if (traceId != null) {
                    MDC.put("traceId", traceId);
                    MDC.put("spanId", spanId);
                    MDC.put("parentId", parentId);
                }
            }
        }).onErrorResume(t -> {
            Rsp rsp;
            if (t instanceof JrpcException) {
                rsp = RspUtil.error((JrpcException) t);
            } else if (t instanceof TimeoutException) {
                rsp = RspUtil.error(Code.CONNECT_TIMEOUT);
            } else {
                rsp = RspUtil.error(Code.CLIENT_ERROR);
            }
            return Mono.just(rsp);
        });
        return mono;
    }

    private static void restore(TransportPool pool, Transport transport) {
        if (pool != null && transport != null) {
            pool.restore(transport);
        }
    }

    private static void invalid(TransportPool pool, Transport transport) {
        if (pool != null && transport != null) {
            pool.invalid(transport);
        }
    }


}
