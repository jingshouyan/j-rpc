package com.github.jingshouyan.jrpc.server.method.handler;

import com.github.jingshouyan.jrpc.base.action.ActionHandler;
import com.github.jingshouyan.jrpc.base.action.ActionInterceptor;
import com.github.jingshouyan.jrpc.base.action.ActionInterceptorHolder;
import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.base.exception.JrpcException;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import com.github.jingshouyan.jrpc.base.util.rsp.RspUtil;
import com.github.jingshouyan.jrpc.server.method.AsyncMethod;
import com.github.jingshouyan.jrpc.server.method.BaseMethod;
import com.github.jingshouyan.jrpc.server.method.Method;
import com.github.jingshouyan.jrpc.server.method.MethodDefinition;
import com.github.jingshouyan.jrpc.server.method.holder.MethodHolder;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.lang.reflect.Type;

/**
 * @author jingshouyan
 * #date 2018/11/1 15:50
 */
@Slf4j
public class ServerActionHandler implements ActionHandler {

    @Override
    public Mono<Rsp> handle(Token token, Req req) {
        ActionHandler handler = this::call;
        for (ActionInterceptor interceptor : ActionInterceptorHolder.getServerInterceptors()) {
            final ActionHandler ah = handler;
            handler = (t, r) -> interceptor.around(t, r, ah);
        }
        Mono<Rsp> single = handler.handle(token, req);
        return single;
    }

    @SuppressWarnings("unchecked")
    private Mono<Rsp> call(Token token, Req req) {
        return Mono.create(monoSink -> {
            Rsp rsp = null;
            String methodName = req.getMethod();
            String param = req.getParam();
            if (null == param) {
                param = "{}";
            }
            try {
                MethodDefinition methodDefinition = MethodHolder.getMethod(methodName);
                Type clazz = methodDefinition.getInputType();
                Object obj;
                try {
                    obj = JsonUtil.toBean(param, clazz);
                } catch (Exception e) {
                    throw new JrpcException(Code.JSON_PARSE_ERROR, e);
                }
                BaseMethod baseMethod = methodDefinition.getMethod();
                baseMethod.validate(obj);
                if (baseMethod instanceof Method) {
                    Method method = (Method) baseMethod;
                    Object data = method.action(token, obj);
                    rsp = RspUtil.success(data);
                } else if (baseMethod instanceof AsyncMethod) {
                    AsyncMethod asyncMethod = (AsyncMethod) baseMethod;
                    Mono<?> mono = asyncMethod.action(token, obj);
                    mono.map(RspUtil::success)
                            .subscribe(monoSink::success,
                                    e -> {
                                        if (e instanceof JrpcException) {
                                            monoSink.success(RspUtil.error((JrpcException) e));
                                        } else {
                                            log.error("call [{}] error.", methodName, e);
                                            monoSink.success(RspUtil.error(Code.SERVER_ERROR));
                                        }
                                    });
                    return;
                }
            } catch (JrpcException e) {
                rsp = RspUtil.error(e);
            } catch (Throwable e) {
                log.error("call [{}] error.", methodName, e);
                rsp = RspUtil.error(Code.SERVER_ERROR);
            }
            monoSink.success(rsp);
        });
    }

}
