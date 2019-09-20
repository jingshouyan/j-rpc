package com.github.jingshouyan.jrpc.base.annotation;

import java.lang.annotation.*;

/**
 * @author jingshouyan
 * #date 2019/9/20 22:10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface JrpcMethod {
    String method();
}
