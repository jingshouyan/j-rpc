package com.github.jingshouyan.jrpc.starter.trace.interceptor;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.B3SingleFormat;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import com.github.jingshouyan.jrpc.base.action.ActionHandler;
import com.github.jingshouyan.jrpc.base.action.ActionInterceptor;
import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.starter.trace.TraceProperties;
import com.github.jingshouyan.jrpc.trace.constant.TraceConstant;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

/**
 * @author jingshouyan
 * #date 2018/11/2 20:00
 */
public class ServerTrace implements TraceConstant, ActionInterceptor {

    private Tracer tracer;
    @Getter
    @Setter
    private TraceProperties properties;

    public ServerTrace(Tracing tracing, TraceProperties properties) {
        this.tracer = tracing.tracer();
        this.properties = properties;
    }

    @Override
    public Mono<Rsp> around(Token token, Req req, ActionHandler handler) {
        final Span span = span(token.get(HEADER_TRACE));

        Tracer.SpanInScope spanInScope = tracer.withSpanInScope(span);
        span.name(req.getMethod())
                .annotate(SR)
                .tag(TAG_METHOD, "" + req.getMethod())
                .tag(TAG_TICKET, "" + token.getTicket())
                .tag(TAG_USER_ID, "" + token.getUserId());

        Mono<Rsp> single = handler.handle(token, req).doOnSuccess(rsp -> {
            if (show(properties.getDataShow(), rsp.success())) {
                span.tag(TAG_PARAM, String.valueOf(req.desensitizedParam()))
                        .tag(TAG_DATA, String.valueOf(rsp.desensitizedResult()));
            }
            if (rsp.getCode() != Code.SUCCESS) {
                span.tag(TAG_ERROR, rsp.getCode() + ":" + rsp.getMessage());
            }
            span.tag(TAG_CODE, String.valueOf(rsp.getCode()))
                    .tag(TAG_MESSAGE, "" + rsp.getMessage())
                    .annotate(SS)
                    .finish();
            spanInScope.close();
        }).doOnError(e -> {
            span.tag(TAG_ERROR, "" + e.getMessage())
                    .annotate(SS)
                    .finish();
            spanInScope.close();
        });
        return single;
    }

    private boolean show(int dataShow, boolean success) {
        switch (dataShow) {
            case TRACE_DATA_SHOW_ALL:
                return true;
            case TRACE_DATA_SHOW_ERROR:
                return !success;
            case TRACE_DATA_SHOW_OFF:
                return false;
            default:
                return false;
        }
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    private Span span(String trace) {
        if (null != trace) {
            TraceContextOrSamplingFlags traceContextOrSamplingFlags = B3SingleFormat.parseB3SingleFormat(trace);
            if (traceContextOrSamplingFlags != null) {
                TraceContext context = traceContextOrSamplingFlags.context();
                if (context != null) {
                    return tracer.joinSpan(context).start();
                }
            }
        }
        return tracer.newTrace().start();
    }

}
