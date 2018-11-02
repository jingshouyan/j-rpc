package com.jing.test.aop;

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
import com.github.jingshouyan.jrpc.base.thrift.ReqBean;
import com.github.jingshouyan.jrpc.base.thrift.TokenBean;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jingshouyan
 * #date 2018/11/1 11:39
 */
//@Component
//@Aspect
@Slf4j(topic = "Trace-Log")
public class TraceAop {

    private static final String SR = "sr";
    private static final String SS = "ss";

    private static final String TAG_TOKEN = "in_token";
    private static final String TAG_PARAM = "in_param";
    private static final String TAG_CODE = "out_code";
    private static final String TAG_MESSAGE = "out_message";
    private static final String TAG_DATA = "out_data";
    private static final String TAG_ERROR = "error";

//    @Pointcut("bean(methodHandler)")
    public void aspect() {
    }
    @Autowired
    private Tracing tracing;

//    @Around("aspect()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Token token = (Token)args[0];
        Req req = (Req) args[1];
        Span span = span(token.getTraceId());
        try (Tracer.SpanInScope spanInScope = tracing.tracer().withSpanInScope(span)) {
            span.name(req.getMethod())
                    .annotate(SR)
                    .tag(TAG_TOKEN,token.toString());

            Object result = joinPoint.proceed();
            if (result instanceof Rsp) {
                Rsp rsp = (Rsp) result;
                span.tag(TAG_CODE,String.valueOf(rsp.getCode()))
                        .tag(TAG_MESSAGE,"" + rsp.getMessage());
                if(rsp.getCode()!= Code.SUCCESS){
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
                    return tracing.tracer().joinSpan(context).start();
//                    return tracing.tracer().newChild(context).start();
                }
            }
        }
        return tracing.tracer().newTrace().start();
    }

    public static void main(String[] args) {
        TraceContext context = TraceContext.newBuilder().build();

    }
}
