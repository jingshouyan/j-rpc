package com.github.jingshouyan.jrpc.client;

import com.github.jingshouyan.jrpc.base.action.ActionHandler;
import com.github.jingshouyan.jrpc.base.action.ActionInterceptor;
import com.github.jingshouyan.jrpc.base.action.ActionInterceptorHolder;
import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.base.exception.JException;
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
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.async.AsyncMethodCallback;

import java.util.List;
import java.util.Map;

/**
 * @author jingshouyan
 * #date 2018/10/26 9:42
 */
@Slf4j
@Getter
public class JrpcClient implements ActionHandler {

    private ClientConfig config;
    private ZkDiscover zkDiscover;
    private TransportProvider transportProvider;

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
    }

    public Map<String,List<ServerInfo>> serverMap(){
        return zkDiscover.serverMap();
    }

    @Override
    public Single<Rsp> handle(Token token, Req req) {
        ActionHandler handler = this::call;
        for (ActionInterceptor interceptor : ActionInterceptorHolder.getClientInterceptors()) {
            handler = interceptor.around(token,req,handler);
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
                            emitter.onSuccess(RspUtil.success());
                        }
                        @Override
                        public void onError(Exception e) {
                            transportProvider.invalid(transport2);
                            JrpcClient.onError(emitter,e);
                        }
                    });
                }else{
                    client.call(tokenBean, reqBean, new AsyncMethodCallback<RspBean>() {
                        @Override
                        public void onComplete(RspBean rspBean) {
                            transportProvider.restore(transport2);
                            emitter.onSuccess(new Rsp(rspBean));
                        }

                        @Override
                        public void onError(Exception e) {
                            transportProvider.invalid(transport2);
                            JrpcClient.onError(emitter,e);
                        }
                    });
                }

                return;
            }catch (JException e) {
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


    private static void onError(SingleEmitter<Rsp> emitter,Exception e) {
        Rsp rsp;
        if(e instanceof JException) {
            rsp = RspUtil.error((JException)e);
        } else {
            rsp = RspUtil.error(Code.CLIENT_ERROR);
            log.error("call rpc error.",e);
        }
        emitter.onSuccess(rsp);
    }

}
