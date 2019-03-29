package com.github.jingshouyan.jrpc.trace.starter.aop;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.context.rxjava2.CurrentTraceContextAssemblyTracking;
import brave.propagation.B3SingleFormat;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import com.github.jingshouyan.jrpc.base.action.ActionHandler;
import com.github.jingshouyan.jrpc.base.action.ActionInterceptor;
import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.trace.starter.TraceProperties;
import com.github.jingshouyan.jrpc.trace.starter.constant.TraceConstant;
import io.reactivex.Single;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @author jingshouyan
 * #date 2018/11/2 20:00
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class ServerTrace implements TraceConstant, ActionInterceptor {

    private Tracer tracer;
    private TraceProperties properties;

    public ServerTrace(Tracing tracing,TraceProperties properties){
        this.tracer = tracing.tracer();
        this.properties = properties;
        CurrentTraceContextAssemblyTracking contextTracking = CurrentTraceContextAssemblyTracking
                .create(tracing.currentTraceContext());
        contextTracking.enable();
    }

    @Override
    public ActionHandler around(Token token, Req req, ActionHandler handler) {
        return (t,r) -> {
            final Span span = span(token.get(HEADER_TRACE));
            Tracer.SpanInScope spanInScope = tracer.withSpanInScope(span);
            span.name(req.getMethod())
                    .annotate(SR)
                    .tag(TAG_METHOD,""+req.getMethod())
                    .tag(TAG_TICKET,""+token.getTicket())
                    .tag(TAG_USER_ID,""+token.getUserId());

            Single<Rsp> single = handler.handle(t,r).doOnSuccess(rsp -> {
                span.tag(TAG_CODE,String.valueOf(rsp.getCode()))
                        .tag(TAG_MESSAGE,"" + rsp.getMessage());
                if(properties.isMore() || !rsp.success()){
                    span.tag(TAG_PARAM,""+req.getParam())
                            .tag(TAG_DATA,""+rsp.getResult());
                }
                if(rsp.getCode() != Code.SUCCESS) {
                    span.tag(TAG_ERROR,rsp.getCode()+":"+rsp.getMessage());
                }
                span.annotate(SS);
                span.finish();
                spanInScope.close();
            });
            return single;
        };
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    private Span span(String trace){
        Span currentSpan = tracer.currentSpan();
        if(currentSpan != null) {
            return tracer.newChild(currentSpan.context()).start();
        }
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
