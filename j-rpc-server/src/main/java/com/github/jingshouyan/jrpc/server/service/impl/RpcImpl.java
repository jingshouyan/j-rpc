package com.github.jingshouyan.jrpc.server.service.impl;

import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.base.exception.JException;
import com.github.jingshouyan.jrpc.base.thrift.ReqBean;
import com.github.jingshouyan.jrpc.base.thrift.RspBean;
import com.github.jingshouyan.jrpc.base.thrift.TokenBean;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import com.github.jingshouyan.jrpc.base.util.rsp.RspUtil;
import com.github.jingshouyan.jrpc.base.util.thread.ThreadLocalUtil;
import com.github.jingshouyan.jrpc.server.method.Method;
import com.github.jingshouyan.jrpc.server.method.holder.MethodHolder;
import com.github.jingshouyan.jrpc.server.service.Rpc;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Type;

/**
 * @author jingshouyan
 * #date 2018/10/22 15:48
 */
@Slf4j
public class RpcImpl implements Rpc {

    @Override
    public RspBean call(TokenBean token, ReqBean req){
        return run(token,req,false);
    }

    @Override
    public void send(TokenBean token, ReqBean req) {
        run(token,req,true);
    }


    private RspBean run(TokenBean tokenBean, ReqBean reqBean,boolean oneway){
        ThreadLocalUtil.setTraceId(tokenBean.getTraceId());
        Token token = new Token(tokenBean);
        Req req = new Req();
        req.setMethod(reqBean.getMethod());
        req.setParam(reqBean.getParam());
        req.setOneway(oneway);
        Rsp rsp = run(token,req);
        //
        RspBean rspBean = new RspBean();
        rspBean.setCode(rsp.getCode());
        rspBean.setMessage(rsp.getMessage());
        String json = JsonUtil.toJsonString(rsp.getData());
        rspBean.setResult(json);

        ThreadLocalUtil.removeTrace();
        return rspBean;
    }

    public Rsp run(Token token,Req req) {
        long start = System.nanoTime();
        Rsp rsp = null;
        String methodName = req.getMethod();
        String param = req.getParam();
        if(StringUtils.isBlank(param)){
            param = "{}";
        }
        try{
            log.info("call [{}] start.",methodName);
            log.info("call [{}] token: {}",methodName,token);
            Method method = MethodHolder.getMethod(methodName);
            Type clazz = method.getInputType();
            Object obj;
            try {
                obj = JsonUtil.toBean(param, clazz);
                log.info("call [{}] param: {}",methodName,obj);
            }catch (Exception e){
                log.info("call [{}] param: {}",methodName,param);
                throw new JException(Code.JSON_PARSE_ERROR,e);
            }
            Object data = method.validAndAction(token,obj);
            rsp = RspUtil.success(data);
        }catch (JException e){
            rsp = RspUtil.error(e);
        }catch (Exception e){
            log.error("call [{}] error.",methodName,e);
            rsp = RspUtil.error(Code.SERVER_ERROR,e);
        }
        long end = System.nanoTime();
        log.info("call [{}] end. {}",methodName,rsp);
        log.info("call [{}] use {} ns",methodName,end - start);
        return rsp;
    }


}
