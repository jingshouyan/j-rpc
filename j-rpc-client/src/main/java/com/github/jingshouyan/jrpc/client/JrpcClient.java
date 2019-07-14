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
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.async.AsyncMethodCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * @author jingshouyan
 * #date 2018/10/26 9:42
 */
@Slf4j
public class JrpcClient implements ActionHandler {

    private ClientConfig config;
    private ZkDiscover zkDiscover;

    private final ExecutorService callbackExec;
    private final BiConsumer<SingleEmitter<Rsp>, Rsp> success;
    private final BiConsumer<SingleEmitter<Rsp>, Exception> error;

    public JrpcClient(ClientConfig config) {
        this.config = config;
        this.zkDiscover = new ZkDiscover(config);

        //callback 执行线程池
        callbackExec = new ThreadPoolExecutor(config.getCallbackThreadPoolSize(),
                config.getCallbackThreadPoolSize(), 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("callback-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        //success 执行方法
        success = (emitter, rsp) -> emitter.onSuccess(rsp);
//                callbackExec.execute(() -> emitter.onSuccess(rsp));
        //error 执行方法
        error = (emitter, e) -> {
            Rsp rsp;
            if (e instanceof JrpcException) {
                rsp = RspUtil.error((JrpcException) e);
            } else if (e instanceof TimeoutException) {
                rsp = RspUtil.error(Code.CONNECT_TIMEOUT);
                log.warn("call rpc timeout.", e);
            } else {
                rsp = RspUtil.error(Code.CLIENT_ERROR);
                log.error("call rpc error.", e);
            }
            success.accept(emitter, rsp);
        };

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
    public Single<Rsp> handle(Token token, Req req) {
        ActionHandler handler = this::call;
        for (ActionInterceptor interceptor : ActionInterceptorHolder.getClientInterceptors()) {
            final ActionHandler ah = handler;
            handler = (t, r) -> interceptor.around(t, r, ah);
        }
        Single<Rsp> single = handler.handle(token, req);
        return single;
    }

    private Single<Rsp> call(Token token, Req req) {
        return Single.create(emitter -> {
            TransportPool pool = null;
            Transport transport = null;
            Rsp rsp;
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
                            success.accept(emitter, RspUtil.success());
                        }

                        @Override
                        public void onError(Exception e) {
                            invalid(poolTmp, transportTmp);
                            error.accept(emitter, e);
                        }
                    });
                } else {
                    client.call(tokenBean, reqBean, new AsyncMethodCallback<RspBean>() {
                        @Override
                        public void onComplete(RspBean rspBean) {
                            restore(poolTmp, transportTmp);
                            success.accept(emitter, new Rsp(rspBean));
                        }

                        @Override
                        public void onError(Exception e) {
                            invalid(poolTmp, transportTmp);
                            error.accept(emitter, e);
                        }
                    });
                }
                return;
            } catch (JrpcException e) {
                restore(pool, transport);
                rsp = RspUtil.error(e);
            } catch (TimeoutException e) {
                restore(pool, transport);
                rsp = RspUtil.error(Code.CONNECT_TIMEOUT);
            } catch (Throwable e) {
                log.error("call rpc error.", e);
                invalid(pool, transport);
                rsp = RspUtil.error(Code.CLIENT_ERROR);
            }
            emitter.onSuccess(rsp);
        });
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
