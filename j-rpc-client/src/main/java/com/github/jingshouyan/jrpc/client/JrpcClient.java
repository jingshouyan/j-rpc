package com.github.jingshouyan.jrpc.client;

import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.base.bean.Trace;
import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.base.exception.JException;
import com.github.jingshouyan.jrpc.base.thrift.Jrpc;
import com.github.jingshouyan.jrpc.base.thrift.ReqBean;
import com.github.jingshouyan.jrpc.base.thrift.RspBean;
import com.github.jingshouyan.jrpc.base.thrift.TokenBean;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import com.github.jingshouyan.jrpc.base.util.rsp.RspUtil;
import com.github.jingshouyan.jrpc.base.util.thread.ThreadLocalUtil;
import com.github.jingshouyan.jrpc.client.config.ClientConfig;
import com.github.jingshouyan.jrpc.client.discover.DiscoverEvent;
import com.github.jingshouyan.jrpc.client.discover.Router;
import com.github.jingshouyan.jrpc.client.discover.ZkDiscover;
import com.github.jingshouyan.jrpc.client.transport.Transport;
import com.github.jingshouyan.jrpc.client.transport.TransportProvider;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;

import java.util.List;
import java.util.Map;

/**
 * @author jingshouyan
 * #date 2018/10/26 9:42
 */
@Slf4j
public class JrpcClient {

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

    public Rsp send(Request request) {
        Transport transport = null;
        Rsp rsp;
        try{
            ServerInfo serverInfo = zkDiscover.getServerInfo(request.getRouter());
            log.info("server {} ==> {}", serverInfo.getName() ,serverInfo.key());
            transport = transportProvider.get(serverInfo);
            TProtocol tProtocol = new TBinaryProtocol(transport.getTTransport());
            Jrpc.Client client = new Jrpc.Client(tProtocol);
            TokenBean tokenBean = request.getToken().tokenBean();
            ReqBean reqBean = new ReqBean(request.getMethod(),request.getParam());
            if(request.isOneway()){
                client.send(tokenBean,reqBean);
                rsp = RspUtil.success();
            }else{
                RspBean rspBean = client.call(tokenBean,reqBean);
                rsp = new Rsp(rspBean);
            }
            transportProvider.restore(transport);
        }catch (JException e) {
            transportProvider.restore(transport);
            rsp = RspUtil.error(e);
        } catch (Exception e) {
            log.error("call rpc error.",e);
            transportProvider.invalid(transport);
            rsp = RspUtil.error(Code.CLIENT_ERROR,e);
        }
        return rsp;
    }

}
