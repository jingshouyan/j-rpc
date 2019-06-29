package com.github.jingshouyan.jrpc.starter.trace.aop;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.context.rxjava2.CurrentTraceContextAssemblyTracking;
import brave.propagation.B3SingleFormat;
import com.github.jingshouyan.jrpc.base.action.ActionHandler;
import com.github.jingshouyan.jrpc.base.action.ActionInterceptor;
import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.starter.trace.TraceProperties;
import com.github.jingshouyan.jrpc.starter.trace.constant.TraceConstant;
import io.reactivex.Single;

/**
 * @author jingshouyan
 * #date 2018/11/2 21:45
 */
public class ClientTrace implements TraceConstant, ActionInterceptor {

    private Tracer tracer;

    public ClientTrace(Tracing tracing, TraceProperties properties) {
        this.tracer = tracing.tracer();
        CurrentTraceContextAssemblyTracking contextTracking = CurrentTraceContextAssemblyTracking
                .create(tracing.currentTraceContext());
        contextTracking.enable();
    }

    @Override
    public Single<Rsp> around(Token token, Req req, ActionHandler handler) {
        final Span span = span().annotate(CS);
        token.set(HEADER_TRACE, traceId(span));
        try (Tracer.SpanInScope spanInScope = tracer.withSpanInScope(span)) {
            span.name(req.getRouter().getServer() + "." + req.getMethod())
                    .tag(TAG_METHOD, "" + req.getMethod())
                    .tag(TAG_TICKET, "" + token.getTicket())
                    .tag(TAG_USER_ID, "" + token.getUserId());
            return handler.handle(token, req).doOnSuccess(rsp -> {
                if (rsp.getCode() != Code.SUCCESS) {
                    span.tag(TAG_ERROR, rsp.getCode() + ":" + rsp.getMessage());
                }
                span.tag(TAG_CODE, String.valueOf(rsp.getCode()))
                        .tag(TAG_MESSAGE, "" + rsp.getMessage())
                        .annotate(CR)
                        .finish();
            }).doOnError(e ->
                    span.tag(TAG_ERROR, "" + e.getMessage())
                            .annotate(CR)
                            .finish()
            );
        }

    }


    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    private Span span() {
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            return tracer.newChild(currentSpan.context()).start();
        }
        return tracer.newTrace().start();
    }

    private String traceId(Span span) {
        return B3SingleFormat.writeB3SingleFormat(span.context());
    }
}
