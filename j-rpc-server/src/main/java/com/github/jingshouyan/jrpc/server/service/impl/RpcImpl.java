package com.github.jingshouyan.jrpc.server.service.impl;

import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.base.exception.JrpcException;
import com.github.jingshouyan.jrpc.base.thrift.ReqBean;
import com.github.jingshouyan.jrpc.base.thrift.RspBean;
import com.github.jingshouyan.jrpc.base.thrift.TokenBean;
import com.github.jingshouyan.jrpc.base.util.rsp.RspUtil;
import com.github.jingshouyan.jrpc.server.method.handler.ServerActionHandler;
import com.github.jingshouyan.jrpc.server.service.Rpc;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.async.AsyncMethodCallback;
import reactor.core.publisher.Mono;

/**
 * @author jingshouyan
 * #date 2018/10/22 15:48
 */
@Slf4j
public class RpcImpl implements Rpc {


    private final ServerActionHandler handler;

    public RpcImpl(ServerActionHandler handler) {
        this.handler = handler;
    }

    @Override
    public void call(TokenBean token, ReqBean req, AsyncMethodCallback<RspBean> resultHandler) {
        Mono<RspBean> rspBeanMono = run(token, req, false);
        rspBeanMono.subscribe(resultHandler::onComplete, e -> {
            Rsp rsp;
            if (e instanceof JrpcException) {
                rsp = RspUtil.error((JrpcException) e);
            } else {
                log.error("server error", e);
                rsp = RspUtil.error(Code.SERVER_ERROR);
            }
            resultHandler.onComplete(toRspBean(rsp));
        });
    }

    @Override
    public void send(TokenBean token, ReqBean req, AsyncMethodCallback<Void> resultHandler) {
        run(token, req, true).subscribe();
    }

    private Mono<RspBean> run(TokenBean tokenBean, ReqBean reqBean, boolean oneway) {
        Token token = new Token(tokenBean);
        Req req = new Req();
        req.setMethod(reqBean.getMethod());
        req.setParam(reqBean.getParam());
        req.setOneway(oneway);
        Mono<Rsp> rspMono = handler.handle(token, req);
        return rspMono.map(this::toRspBean);
    }

    private RspBean toRspBean(Rsp rsp) {
        RspBean rspBean = new RspBean();
        rspBean.setCode(rsp.getCode());
        rspBean.setMessage(rsp.getMessage());
        rspBean.setResult(rsp.getResult());
        return rspBean;
    }


}
