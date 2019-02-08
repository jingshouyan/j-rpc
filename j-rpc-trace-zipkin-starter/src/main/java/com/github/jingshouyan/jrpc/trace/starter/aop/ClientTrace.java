package com.github.jingshouyan.jrpc.trace.starter.aop;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.B3SingleFormat;
import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.trace.starter.TraceProperties;
import com.github.jingshouyan.jrpc.trace.starter.constant.TraceConstant;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author jingshouyan
 * #date 2018/11/2 21:45
 */
@Aspect
public class ClientTrace implements TraceConstant {

    private Tracer tracer;
    private TraceProperties properties;

    public ClientTrace(Tracing tracing,TraceProperties properties){
        this.tracer = tracing.tracer();

        this.properties = properties;
    }

    @Pointcut("bean(jrpcClient) && execution(* *.handle(..))")
    public void aspect() {
    }


    @Around("aspect()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        if(!properties.isOn()) {
            return joinPoint.proceed();
        }
        Object[] args = joinPoint.getArgs();
        Token token = (Token)args[0];
        Req req = (Req) args[1];
        Span span = span();
        token.set(HEADER_TRACE,traceId(span));
        try (Tracer.SpanInScope spanInScope = tracer.withSpanInScope(span)) {
            span.name("call "+req.getRouter().getServer()+":"+req.getMethod())
                    .annotate(CS)
                    .tag(TAG_METHOD,""+req.getMethod())
                    .tag(TAG_USER_ID, ""+token.getUserId())
                    .tag(TAG_TICKET, ""+token.getTicket());

            Object result = joinPoint.proceed();
            if (result instanceof Rsp) {
                Rsp rsp = (Rsp) result;
                if(rsp.getCode() != Code.SUCCESS) {
                    span.tag(TAG_ERROR,rsp.getCode()+":"+rsp.getMessage());
                }
            }
            span.annotate(CR);
            return result;
        }catch (Throwable e){
            span.tag(TAG_ERROR,e.getClass().getSimpleName()+":"+e.getMessage());
            throw e;
        }finally {
            span.finish();
        }
    }


    private Span span(){
        Span currentSpan = tracer.currentSpan();
        if(currentSpan != null) {
            return tracer.newChild(currentSpan.context()).start();
        }
        return tracer.newTrace().start();
    }

    private String traceId(Span span){
        return B3SingleFormat.writeB3SingleFormat(span.context());
    }
}
