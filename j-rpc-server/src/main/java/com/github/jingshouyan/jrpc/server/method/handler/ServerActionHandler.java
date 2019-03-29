package com.github.jingshouyan.jrpc.server.method.handler;

import com.github.jingshouyan.jrpc.base.action.ActionHandler;
import com.github.jingshouyan.jrpc.base.action.ActionInterceptor;
import com.github.jingshouyan.jrpc.base.action.ActionInterceptorHolder;
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
        String method = req.getMethod();

        ActionHandler handler = (t,r)-> {
            log.debug("action [{}] token: {}",method,token);
            log.debug("action [{}] param: {}.",method,req.getParam());
            return this.call(t,r);
        };

        for (ActionInterceptor interceptor : ActionInterceptorHolder.getServerInterceptors()) {
            handler = interceptor.around(token,req,handler);
        }
        Single<Rsp> single = handler.handle(token,req).doAfterSuccess(rsp -> {
            long end = System.nanoTime();
            log.debug("action [{}] end. rsp: {}",req.getMethod(), rsp.json());
            log.debug("action [{}] use {} ns",req.getMethod(), end - start);
        });
        return single;
    }

    private Single<Rsp> call(Token token, Req req) {
        return Single.create(emitter -> {
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
                }catch (Exception e){
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
                    single.map(RspUtil::success).subscribe(emitter::onSuccess);
                }
            }catch (JException e){
                rsp = RspUtil.error(e);
            }catch (Exception e){
                log.error("call [{}] error.",methodName,e);
                rsp = RspUtil.error(Code.SERVER_ERROR);
            }
            emitter.onSuccess(rsp);
        });
    }

}
