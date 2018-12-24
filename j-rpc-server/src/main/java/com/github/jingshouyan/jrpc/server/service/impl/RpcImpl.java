package com.github.jingshouyan.jrpc.server.service.impl;

import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.base.concurrent.threadlocal.TokenContextLocal;
import com.github.jingshouyan.jrpc.base.thrift.ReqBean;
import com.github.jingshouyan.jrpc.base.thrift.RspBean;
import com.github.jingshouyan.jrpc.base.thrift.TokenBean;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import com.github.jingshouyan.jrpc.server.method.handler.ServerActionHandler;
import com.github.jingshouyan.jrpc.server.service.Rpc;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jingshouyan
 * #date 2018/10/22 15:48
 */
@Slf4j
public class RpcImpl implements Rpc {

    private final ServerActionHandler handler;

    public RpcImpl(ServerActionHandler handler){
        this.handler = handler;
    }

    @Override
    public RspBean call(TokenBean token, ReqBean req){
        return run(token,req,false);
    }

    @Override
    public void send(TokenBean token, ReqBean req) {
        run(token,req,true);
    }


    private RspBean run(TokenBean tokenBean, ReqBean reqBean,boolean oneway){
        Token token = new Token(tokenBean);
        TokenContextLocal.getInstance().set(token);
        Req req = new Req();
        req.setMethod(reqBean.getMethod());
        req.setParam(reqBean.getParam());
        req.setOneway(oneway);
        Rsp rsp = handler.handle(token,req);
        //
        RspBean rspBean = new RspBean();
        rspBean.setCode(rsp.getCode());
        rspBean.setMessage(rsp.getMessage());
        String json = JsonUtil.toJsonString(rsp.getData());
        rspBean.setResult(json);
        TokenContextLocal.getInstance().remove();
        return rspBean;
    }


}
