package com.github.jingshouyan.jrpc.trace.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SpanX {
    String spanName() default "";

    boolean showData() default false;
}
