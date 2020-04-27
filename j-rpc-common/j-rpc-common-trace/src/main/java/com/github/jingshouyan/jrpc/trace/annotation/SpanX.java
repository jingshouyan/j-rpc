package com.github.jingshouyan.jrpc.trace.annotation;

import java.lang.annotation.*;

/**
 * @author jingshouyan
 * 11/29/18 5:38 PM
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SpanX {
    String spanName() default "";

    boolean showData() default false;
}
