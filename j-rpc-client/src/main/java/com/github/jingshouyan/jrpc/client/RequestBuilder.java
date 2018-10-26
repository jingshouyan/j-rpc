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
public class RequestBuilder {

    private ClientConfig config;
    private ZkDiscover zkDiscover;
    private TransportProvider transportProvider;

    public RequestBuilder(ClientConfig config){
        this.config = config;
        this.zkDiscover = new ZkDiscover(config.getZkHost(), config.getZkRoot());
        GenericObjectPoolConfig cfg = new GenericObjectPoolConfig();
        cfg.setMinIdle(config.getPoolMinIdle());
        cfg.setMaxIdle(config.getPoolMaxIdle());
        cfg.setMaxTotal(config.getPoolMaxTotal());
        this.transportProvider = new TransportProvider(cfg);
    }

    public Request newRequest(){
        return new Request(this);
    }

    public Map<String,List<ServerInfo>> serverMap(){
        return zkDiscover.serverMap();
    }

    private Rsp send(Request request) {
        Transport transport = null;
        Rsp rsp;
        try{
            ServerInfo serverInfo = zkDiscover.getServerInfo(request.getRouter());
            log.info("server {} ==> {}", serverInfo.getName() ,serverInfo.key());
            Trace trace = ThreadLocalUtil.getTrace();
            String traceId = trace.newTraceId();

            transport = transportProvider.get(serverInfo);
            TProtocol tProtocol = new TBinaryProtocol(transport.getTTransport());
            Jrpc.Client client = new Jrpc.Client(tProtocol);
            TokenBean tokenBean = request.getToken().tokenBean();
            tokenBean.setTraceId(traceId);
            ReqBean reqBean = new ReqBean(request.method,request.param);
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





    @Getter@ToString(exclude = {"builder"})
    public class Request{
        private Router router = new Router();

        private Token token = new Token();

        private String method;

        private String param;

        private boolean oneway;

        private RequestBuilder builder;

        private Request(RequestBuilder builder) {
            this.builder = builder;
        }

        public Request setToken(Token token) {
            this.token = token;
            return this;
        }

        public Request setMethod(String method){
            this.method = method;
            return this;
        }
        public Request setParam(String param){
            this.param = param;
            return this;
        }
        public Request setParamObj(Object paramObj){
            this.param = JsonUtil.toJsonString(paramObj);
            return this;
        }
        public Request setOneway(boolean oneway){
            this.oneway = oneway;
            return this;
        }

        public Request setServer(String server){
            router.setServer(server);
            return this;
        }

        public Request setVersion(String version){
            router.setVersion(version);
            return this;
        }

        public Request setInstance(String instance){
            router.setInstance(instance);
            return this;
        }

        public Rsp send(){
            log.info("call rpc req: {}",this);
            Rsp rsp = builder.send(this);
            log.info("call rpc rsp: {}",rsp);
            return rsp;
        }

    }


}
