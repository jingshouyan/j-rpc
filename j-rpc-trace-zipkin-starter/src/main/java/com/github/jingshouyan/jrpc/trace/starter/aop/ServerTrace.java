package com.github.jingshouyan.jrpc.trace.starter.aop;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.B3SingleFormat;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
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
 * #date 2018/11/2 20:00
 */
@Aspect
public class ServerTrace implements TraceConstant {

    private Tracer tracer;
    private TraceProperties properties;

    public ServerTrace(Tracing tracing, TraceProperties properties){
        this.tracer = tracing.tracer();
        this.properties = properties;
    }

    @Pointcut("bean(serverActionHandler) && execution(* *.handle(..))")
    public void aspect() {
    }


    @Around("aspect()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Token token = (Token)args[0];
        Req req = (Req) args[1];
        Span span = span(token.get(HEADER_TRACE));
        try (Tracer.SpanInScope spanInScope = tracer.withSpanInScope(span)) {
            span.name(req.getMethod())
                    .annotate(SR)
                    .tag(TAG_METHOD,""+req.getMethod())
                    .tag(TAG_TICKET,""+token.getTicket())
                    .tag(TAG_USER_ID,""+token.getUserId());

            Object result = joinPoint.proceed();
            if (result instanceof Rsp) {
                Rsp rsp = (Rsp) result;
                span.tag(TAG_CODE,String.valueOf(rsp.getCode()))
                        .tag(TAG_MESSAGE,"" + rsp.getMessage());
                if(properties.isMore() || rsp.getCode()!= Code.SUCCESS){
                    span.tag(TAG_PARAM,""+req.getParam())
                            .tag(TAG_DATA,""+rsp.getData());
                }
            }
            span = span.annotate(SS);
            return result;
        }catch (Throwable e){
            span.tag(TAG_ERROR,e.getClass().getSimpleName()+":"+e.getMessage());
            throw e;
        }finally {
            span.finish();
        }
    }


    private Span span(String trace){
        if(null != trace){
            TraceContextOrSamplingFlags traceContextOrSamplingFlags =B3SingleFormat.parseB3SingleFormat(trace);
            if(traceContextOrSamplingFlags !=null ){
                TraceContext context = traceContextOrSamplingFlags.context();
                if(context != null){
                    return tracer.joinSpan(context).start();
                }
            }
        }
        return tracer.newTrace().start();
    }

}
