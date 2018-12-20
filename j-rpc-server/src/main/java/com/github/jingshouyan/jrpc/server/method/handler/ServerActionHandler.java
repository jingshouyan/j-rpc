package com.github.jingshouyan.jrpc.server.method.handler;

import com.github.jingshouyan.jrpc.base.action.ActionHandler;
import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.base.exception.JException;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import com.github.jingshouyan.jrpc.base.util.rsp.RspUtil;
import com.github.jingshouyan.jrpc.server.method.Method;
import com.github.jingshouyan.jrpc.server.method.holder.MethodHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Type;

/**
 * @author jingshouyan
 * #date 2018/11/1 15:50
 */
@Slf4j
public class ServerActionHandler implements ActionHandler {

    @Override
    public Rsp handle(Token token, Req req) {
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
            method.validate(obj);
            Object data = method.action(token,obj);
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
