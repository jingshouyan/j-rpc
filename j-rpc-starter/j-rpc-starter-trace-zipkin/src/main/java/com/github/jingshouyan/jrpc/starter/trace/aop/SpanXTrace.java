package com.github.jingshouyan.jrpc.starter.trace.aop;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import com.github.jingshouyan.jrpc.trace.annotation.SpanX;
import com.github.jingshouyan.jrpc.trace.constant.TraceConstant;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;

/**
 * @author jingshouyan
 * 11/29/18 5:26 PM
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class SpanXTrace implements TraceConstant {


    private Tracer tracer;

    public SpanXTrace(Tracing tracing) {
        this.tracer = tracing.tracer();
    }


    @Around("execution(@com.github.jingshouyan.jrpc.trace.annotation.SpanX * *(..)) && @annotation(spanX)")
    public Object around(ProceedingJoinPoint joinPoint, SpanX spanX) throws Throwable {
        Span span = span();
        String spanName = spanX.spanName();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        if ("".equals(spanName)) {
            spanName = method.getName();
        }
        try (Tracer.SpanInScope spanInScope = tracer.withSpanInScope(span)) {
            span.name(spanName)
                    .annotate(CS);
            Object result = joinPoint.proceed();
            span.tag(CALL_PATH, method.toString());
            if (spanX.showData()) {
                Object[] args = joinPoint.getArgs();
                for (int i = 0; i < args.length; i++) {
                    span.tag(TAG_ARG_PREFIX + i, "" + args[i]);
                }
                span.tag(TAG_RESULT, "" + result);
            }
            span.annotate(CR);
            return result;
        } catch (Throwable e) {
            span.tag(TAG_ERROR, e.getClass().getSimpleName() + ":" + e.getMessage());
            throw e;
        } finally {
            span.finish();
        }
    }

    private Span span() {
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            return tracer.newChild(currentSpan.context()).start();
        }
        return tracer.newTrace().start();
    }
}
