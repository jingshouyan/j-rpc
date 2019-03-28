package com.github.jingshouyan.jrpc.server.service.impl;

import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.base.thrift.ReqBean;
import com.github.jingshouyan.jrpc.base.thrift.RspBean;
import com.github.jingshouyan.jrpc.base.thrift.TokenBean;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import com.github.jingshouyan.jrpc.server.method.handler.ServerActionHandler;
import com.github.jingshouyan.jrpc.server.service.Rpc;
import io.reactivex.Single;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.async.AsyncMethodCallback;

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
    public void call(TokenBean token, ReqBean req, AsyncMethodCallback<RspBean> resultHandler) {
        Single<RspBean> rspBeanSingle = run(token,req,false);
        rspBeanSingle.subscribe(resultHandler::onComplete);
    }

    @Override
    public void send(TokenBean token, ReqBean req, AsyncMethodCallback<Void> resultHandler) {
        run(token,req,true).subscribe();
    }

    private Single<RspBean> run(TokenBean tokenBean, ReqBean reqBean, boolean oneway){
        Token token = new Token(tokenBean);
        Req req = new Req();
        req.setMethod(reqBean.getMethod());
        req.setParam(reqBean.getParam());
        req.setOneway(oneway);
        Single<Rsp> rspSingle = handler.handle(token,req);
        return rspSingle.map(rsp -> {
            RspBean rspBean = new RspBean();
            rspBean.setCode(rsp.getCode());
            rspBean.setMessage(rsp.getMessage());
            if(rsp.getResult() != null) {
                rspBean.setResult(rsp.getResult());
            } else {
                String json = JsonUtil.toJsonString(rsp.getData());
                rspBean.setResult(json);
            }
            return rspBean;
        });
    }


}
