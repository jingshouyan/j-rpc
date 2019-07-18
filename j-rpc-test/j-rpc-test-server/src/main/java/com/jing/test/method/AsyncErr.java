package com.jing.test.method;

import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.server.method.AsyncMethod;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author jingshouyan
 * #date 2019/3/29 20:21
 */
@Component("asyncErr")
public class AsyncErr implements AsyncMethod<String, String> {

    @Override
    public Mono<String> action(Token token, String s) {
        return Mono.fromCallable(() -> {
            throw new RuntimeException("3123123");
        });
    }
}