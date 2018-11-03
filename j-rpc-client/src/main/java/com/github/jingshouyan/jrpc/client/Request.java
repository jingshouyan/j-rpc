package com.github.jingshouyan.jrpc.client;

import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import com.github.jingshouyan.jrpc.base.bean.Router;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jingshouyan
 * #date 2018/11/2 14:41
 */
@Getter
@ToString(exclude = {"client"})
@Slf4j
public class Request{
    private Router router = new Router();

    private Token token = new Token();

    private Req req = new Req();

    private JrpcClient client;


    public static Request newInstance(){
        return new Request();
    }

    public Request setToken(Token token) {
        this.token = token;
        return this;
    }

    public Request setClient(JrpcClient client){
        this.client = client;
        return this;
    }

    public Request setMethod(String method){
        req.setMethod(method);
        return this;
    }
    public Request setParam(String param){
        req.setParam(param);
        return this;
    }
    public Request setParamObj(Object paramObj){
        String param = JsonUtil.toJsonString(paramObj);
        req.setParam(param);
        return this;
    }
    public Request setOneway(boolean oneway){
        req.setOneway(oneway);
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
        req.setRouter(router);
        long start = System.nanoTime();
        log.info("call rpc req: {}",this);
        Rsp rsp = client.handle(token,req);
        log.info("call rpc rsp: {}",rsp);
        long end = System.nanoTime();
        log.info("call rpc use: {}ns", end - start);
        return rsp;
    }

}