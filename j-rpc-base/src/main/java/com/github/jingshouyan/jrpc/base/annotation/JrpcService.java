package com.github.jingshouyan.jrpc.base.annotation;

import java.lang.annotation.*;

/**
 * @author 29017
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface JrpcService {
    String server();

    String version() default "";
}
