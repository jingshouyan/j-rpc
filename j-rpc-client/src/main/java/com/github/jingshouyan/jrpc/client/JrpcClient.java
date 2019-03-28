package com.github.jingshouyan.jrpc.client;

import com.github.jingshouyan.jrpc.base.action.ActionHandler;
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
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

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
        long start = System.nanoTime();
        log.debug("call rpc token: {}",token);
        log.debug("call rpc req: {}",req);
        Transport transport = null;
        Rsp rsp;
        try{
            ServerInfo serverInfo = zkDiscover.getServerInfo(req.getRouter());
            log.debug("server {} ==> {},{}:{}",
                    serverInfo.getName() ,serverInfo.getInstance(),serverInfo.getHost(),serverInfo.getPort());
            transport = transportProvider.get(serverInfo);
            Jrpc.Client client = transport.getClient();
            log.debug("get client use {} ns",System.nanoTime() - start);
            TokenBean tokenBean = token.tokenBean();
            ReqBean reqBean = req.reqBean();
            if(req.isOneway()){
                client.send(tokenBean,reqBean);
                rsp = RspUtil.success();
            }else{
                RspBean rspBean = client.call(tokenBean,reqBean);
                rsp = new Rsp(rspBean);
            }
            log.debug("send use {} ns",System.nanoTime() - start);
            transportProvider.restore(transport);
        }catch (JException e) {
            transportProvider.restore(transport);
            rsp = RspUtil.error(e);
        } catch (Exception e) {
            log.error("call rpc error.",e);
            transportProvider.invalid(transport);
            rsp = RspUtil.error(Code.CLIENT_ERROR);
        }
        log.debug("call rpc rsp: {}",rsp);
        long end = System.nanoTime();
        log.debug("call rpc use: {}ns", end - start);
        return Single.just(rsp);
    }

}
