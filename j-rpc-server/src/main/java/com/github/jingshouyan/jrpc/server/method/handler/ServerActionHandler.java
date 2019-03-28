package com.github.jingshouyan.jrpc.server.method.handler;

import com.github.jingshouyan.jrpc.base.action.ActionHandler;
import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.base.exception.JException;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import com.github.jingshouyan.jrpc.base.util.rsp.RspUtil;
import com.github.jingshouyan.jrpc.server.method.AsyncMethod;
import com.github.jingshouyan.jrpc.server.method.BaseMethod;
import com.github.jingshouyan.jrpc.server.method.Method;
import com.github.jingshouyan.jrpc.server.method.holder.MethodHolder;
import io.reactivex.Single;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

/**
 * @author jingshouyan
 * #date 2018/11/1 15:50
 */
@Slf4j
public class ServerActionHandler implements ActionHandler {

    @Override
    public Single<Rsp> handle(Token token, Req req) {
        long start = System.nanoTime();
        log.debug("call [{}] start.",req.getMethod());
        log.debug("call [{}] token: {}",req.getMethod(),token);
        Single<Rsp> single = call(token,req);
        Single<Rsp> single2 = single.doAfterSuccess(rsp -> {
            long end = System.nanoTime();
            log.debug("call [{}] end. {}",req.getMethod(), rsp.json());
            log.debug("call [{}] use {} ns",req.getMethod(), end - start);
        });
        return single2;
    }

    private Single<Rsp> call(Token token, Req req) {
        Rsp rsp = null;
        String methodName = req.getMethod();
        String param = req.getParam();
        if(null == param){
            param = "{}";
        }
        try{
            BaseMethod baseMethod = MethodHolder.getMethod(methodName);
            Type clazz = baseMethod.getInputType();
            Object obj;
            try {
                obj = JsonUtil.toBean(param, clazz);
                log.debug("call [{}] param: {}",methodName,obj);
            }catch (Exception e){
                log.debug("call [{}] param: {}",methodName,param);
                throw new JException(Code.JSON_PARSE_ERROR,e);
            }
            baseMethod.validate(obj);
            if(baseMethod instanceof Method) {
                Method method = (Method) baseMethod;
                Object data = method.action(token,obj);
                rsp = RspUtil.success(data);
            } else if(baseMethod instanceof AsyncMethod){
                AsyncMethod asyncMethod = (AsyncMethod) baseMethod;
                Single<?> single = asyncMethod.action(token,obj);
                return single.map(RspUtil::success);
            }
        }catch (JException e){
            rsp = RspUtil.error(e);
        }catch (Exception e){
            log.error("call [{}] error.",methodName,e);
            rsp = RspUtil.error(Code.SERVER_ERROR);
        }
        return Single.just(rsp);
    }

}
