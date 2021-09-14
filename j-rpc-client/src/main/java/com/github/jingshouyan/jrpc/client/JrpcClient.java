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
import com.github.jingshouyan.jrpc.base.info.ConnectInfo;
import com.github.jingshouyan.jrpc.base.thrift.Jrpc;
import com.github.jingshouyan.jrpc.base.thrift.ReqBean;
import com.github.jingshouyan.jrpc.base.thrift.RspBean;
import com.github.jingshouyan.jrpc.base.thrift.TokenBean;
import com.github.jingshouyan.jrpc.base.util.rsp.RspUtil;
import com.github.jingshouyan.jrpc.client.config.ConnectConf;
import com.github.jingshouyan.jrpc.client.config.PoolConf;
import com.github.jingshouyan.jrpc.client.pool.KeyedTransportPool;
import com.github.jingshouyan.jrpc.client.transport.Transport;
import com.github.jingshouyan.jrpc.registry.NodeEvent;
import com.github.jingshouyan.jrpc.registry.NodeManager;
import com.github.jingshouyan.jrpc.registry.node.NodeGroup;
import com.github.jingshouyan.jrpc.registry.node.SvrNode;
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

    private final KeyedTransportPool pool;
    private final NodeManager nodeManager;

    public JrpcClient(KeyedTransportPool pool, NodeManager nodeManager) {
        this.pool = pool;
        this.nodeManager = nodeManager;
        // 节点移除时,清理连接池
        this.nodeManager.addListener((event, node) -> {
            if (event == NodeEvent.REMOVE) {
                pool.clear(node.getConnectInfo());
            }
        });
    }

    public Map<String, List<ServerInfo>> serverMap() {
        // todo 换成 nodeManager 数据
        Map<String, NodeGroup> nodeGroupMap = nodeManager.getGroupMap();

        Map<String, List<ServerInfo>> map = new HashMap<>(nodeGroupMap.size());

        for (Map.Entry<String, NodeGroup> entry : nodeGroupMap.entrySet()) {
            String key = entry.getKey();
            List<ServerInfo> value = entry.getValue().getNodes().stream()
                    .map(node -> {
                        ServerInfo serverInfo = new ServerInfo();
                        serverInfo.setName(node.getName());
                        serverInfo.setVersion(node.getVersion());
                        serverInfo.setHost(node.getConnectInfo().getHost());
                        serverInfo.setPort(node.getConnectInfo().getPort());


                        return serverInfo;
                    })
                    .collect(Collectors.toList());
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
        return handler.handle(token, req);
    }

    private Mono<Rsp> call(Token token, Req req) {
        Mono<Rsp> mono = Mono.create(monoSink -> {
            Transport transport = null;
            try {
                SvrNode svrNode = nodeManager.getNode(req.getRouter());
                ConnectInfo conn = svrNode.getConnectInfo();
                transport = pool.borrowObject(conn);
                Transport transportTmp = transport;
                Jrpc.AsyncClient client = transport.getAsyncClient();
                TokenBean tokenBean = token.tokenBean();
                ReqBean reqBean = req.reqBean();
                if (req.isOneway()) {
                    client.send(tokenBean, reqBean, new AsyncMethodCallback<Void>() {
                        @Override
                        public void onComplete(Void aVoid) {
                            restore(transportTmp);
                            monoSink.success(RspUtil.success());
                        }

                        @Override
                        public void onError(Exception e) {
                            invalid(transportTmp);
                            monoSink.error(e);
                        }
                    });
                } else {
                    client.call(tokenBean, reqBean, new AsyncMethodCallback<RspBean>() {
                        @Override
                        public void onComplete(RspBean rspBean) {
                            restore(transportTmp);
                            monoSink.success(new Rsp(rspBean));
                        }

                        @Override
                        public void onError(Exception e) {
                            invalid(transportTmp);
                            monoSink.error(e);
                        }
                    });
                }
            } catch (Throwable e) {
                log.error("call rpc error.", e);
                invalid(transport);
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

    private void restore(Transport transport) {
        pool.returnObject(transport);
    }

    private void invalid(Transport transport) {
        try {
            pool.invalidateObject(transport);
        } catch (Exception e) {
            log.warn("invalid transport error,[{}]", transport.getKey(), e);
        }
    }


}
