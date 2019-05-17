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
import com.github.jingshouyan.jrpc.client.discover.DiscoverEvent;
import com.github.jingshouyan.jrpc.client.discover.ZkDiscover;
import com.github.jingshouyan.jrpc.client.transport.Transport;
import com.github.jingshouyan.jrpc.client.transport.TransportProvider;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.async.AsyncMethodCallback;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * @author jingshouyan
 * #date 2018/10/26 9:42
 */
@Slf4j
public class JrpcClient implements ActionHandler {

    private ClientConfig config;
    private ZkDiscover zkDiscover;
    private final TransportProvider transportProvider;

    private final ExecutorService callbackExec;
    private final BiConsumer<SingleEmitter<Rsp>,Rsp> success;
    private final BiConsumer<SingleEmitter<Rsp>,Exception> error;
    public JrpcClient(ClientConfig config){
        this.config = config;
        this.zkDiscover = new ZkDiscover(config.getZkHost(), config.getZkRoot());
        GenericObjectPoolConfig cfg = new GenericObjectPoolConfig();
        cfg.setMinIdle(config.getPoolMinIdle());
        cfg.setMaxIdle(config.getPoolMaxIdle());
        cfg.setMaxTotal(config.getPoolMaxTotal());
        cfg.setTestWhileIdle(true);
        cfg.setTimeBetweenEvictionRunsMillis(10000);
        this.transportProvider = new TransportProvider(cfg);
        zkDiscover.addListener((event, serverInfo) -> {
            if(event == DiscoverEvent.REMOVE){
                this.transportProvider.close(serverInfo);
            }
        });

        //callback 执行线程池
        callbackExec = new ThreadPoolExecutor(config.getCallbackThreadPoolSize(),
                config.getCallbackThreadPoolSize(), 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("callback-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        //success 执行方法
        success = (emitter, rsp) -> callbackExec.execute(() -> emitter.onSuccess(rsp));
        //error 执行方法
        error = (emitter, e) -> {
            Rsp rsp;
            if(e instanceof JrpcException) {
                rsp = RspUtil.error((JrpcException)e);
            } else {
                rsp = RspUtil.error(Code.CLIENT_ERROR);
                log.error("call rpc error.",e);
            }
            success.accept(emitter, rsp);
        };

    }

    public Map<String,List<ServerInfo>> serverMap(){
        return zkDiscover.serverMap();
    }

    @Override
    public Single<Rsp> handle(Token token, Req req) {
        ActionHandler handler = this::call;
        for (ActionInterceptor interceptor : ActionInterceptorHolder.getClientInterceptors()) {
            final ActionHandler ah = handler;
            handler = (t, r) -> interceptor.around(t, r, ah);
        }
        Single<Rsp> single = handler.handle(token,req);
        return single;
    }

    private Single<Rsp> call(Token token, Req req) {
        return Single.create(emitter -> {
            Transport transport = null;
            Rsp rsp;
            try{
                ServerInfo serverInfo = zkDiscover.getServerInfo(req.getRouter());
                transport = transportProvider.get(serverInfo);
                // 必须在接收完数据才能放连接池.
                Transport transport2 = transport;
                Jrpc.AsyncClient client = transport.getAsyncClient();
                TokenBean tokenBean = token.tokenBean();
                ReqBean reqBean = req.reqBean();
                if(req.isOneway()){
                    client.send(tokenBean, reqBean, new AsyncMethodCallback<Void>() {
                        @Override
                        public void onComplete(Void aVoid) {
                            transportProvider.restore(transport2);
                            success.accept(emitter, RspUtil.success());
                        }
                        @Override
                        public void onError(Exception e) {
                            transportProvider.invalid(transport2);
                            error.accept(emitter,e);
                        }
                    });
                }else{
                    client.call(tokenBean, reqBean, new AsyncMethodCallback<RspBean>() {
                        @Override
                        public void onComplete(RspBean rspBean) {
                            transportProvider.restore(transport2);
                            success.accept(emitter,new Rsp(rspBean));
                        }

                        @Override
                        public void onError(Exception e) {
                            transportProvider.invalid(transport2);
                            error.accept(emitter,e);
                        }
                    });
                }
                return;
            }catch (JrpcException e) {
                transportProvider.restore(transport);
                rsp = RspUtil.error(e);
            } catch (Exception e) {
                log.error("call rpc error.",e);
                transportProvider.invalid(transport);
                rsp = RspUtil.error(Code.CLIENT_ERROR);
            }
            emitter.onSuccess(rsp);
        });
    }


}
