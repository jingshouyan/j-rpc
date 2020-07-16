package com.github.jingshouyan.jrpc.starter.trace.interceptor;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.B3SingleFormat;
import brave.sampler.CountingSampler;
import com.github.jingshouyan.jrpc.base.action.ActionHandler;
import com.github.jingshouyan.jrpc.base.action.ActionInterceptor;
import com.github.jingshouyan.jrpc.base.bean.Req;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.starter.trace.TraceProperties;
import com.github.jingshouyan.jrpc.trace.constant.TraceConstant;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author jingshouyan
 * #date 2018/11/2 21:45
 */
public class ClientTrace implements TraceConstant, ActionInterceptor {

    private Tracer tracer;

    @Getter
    @Setter
    private TraceProperties properties;

    @Getter
    private float rate;

    public ClientTrace(Tracing tracing, TraceProperties properties) {
        this.tracer = tracing.tracer();
        this.properties = properties;
        this.rate = properties.getRate();
    }

    public void setRate(float rate) {
        this.rate = rate;
        tracer = tracer.withSampler(CountingSampler.create(rate));
    }

    @Override
    public Mono<Rsp> around(Token token, Req req, ActionHandler handler) {
        final Span span = span().annotate(CS);
        token.set(HEADER_TRACE, traceId(span));

        try (Tracer.SpanInScope spanInScope = tracer.withSpanInScope(span)) {
            span.name(req.getRouter().getServer() + "." + req.getMethod())
                    .tag(TAG_METHOD, "" + req.getMethod())
                    .tag(TAG_TICKET, "" + token.getTicket())
                    .tag(TAG_USER_ID, "" + token.getUserId());
            for (Map.Entry<String, String> entry : token.getHeaders().entrySet()) {
                span.tag(TAG_HEADER_PREFIX + entry.getKey(), entry.getValue());
            }
            return handler.handle(token, req).doOnSuccess(rsp -> {
                if (!rsp.success()) {
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
