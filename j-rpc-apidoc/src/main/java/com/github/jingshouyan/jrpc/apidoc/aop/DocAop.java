package com.github.jingshouyan.jrpc.apidoc.aop;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.B3SingleFormat;
import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.client.Request;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jingshouyan
 * #date 2018/11/2 14:01
 */
@Component
@Aspect
@Slf4j(topic = "Trace-Log")
public class DocAop {

    private static final String TAG_TOKEN = "in_token";
    private static final String TAG_PARAM = "in_param";
    private static final String TAG_CODE = "out_code";
    private static final String TAG_MESSAGE = "out_message";
    private static final String TAG_DATA = "out_data";
    private static final String TAG_ERROR = "error";

    @Pointcut("bean(jrpcClient) && execution(* *.send(..))")
    public void aspect() {
    }
    @Autowired
    private Tracing tracing;

    @Around("aspect()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Request request = (Request)args[0];
        Span span = span();
        try (Tracer.SpanInScope spanInScope = tracing.tracer().withSpanInScope(span)) {
            span.name("call_"+request.getRouter().getServer()+":"+request.getMethod())
                    .annotate("cs")
                    .tag(TAG_TOKEN, ""+request.getToken());
            request.getToken().setTraceId(traceId(span));
            Object result = joinPoint.proceed();
            if (result instanceof Rsp) {
                Rsp rsp = (Rsp) result;
                span.tag(TAG_CODE,String.valueOf(rsp.getCode()))
                        .tag(TAG_MESSAGE,"" + rsp.getMessage());
                if(rsp.getCode()!= Code.SUCCESS){
                    span.tag(TAG_PARAM,""+request.getParam())
                            .tag(TAG_DATA,""+rsp.getResult());
                }
            }
            span.annotate("cr");
            return result;
        }catch (Throwable e){
            span.tag(TAG_ERROR,e.getClass().getSimpleName()+":"+e.getMessage());
            throw e;
        }finally {
            span.finish();
        }
    }

    private Span span(){
        Span currentSpan = tracing.tracer().currentSpan();
        if(currentSpan != null) {
            return tracing.tracer().newChild(currentSpan.context()).start();
        }
        return tracing.tracer().newTrace().start();
    }

    private String traceId(Span span){
        return B3SingleFormat.writeB3SingleFormat(span.context());
    }

}
