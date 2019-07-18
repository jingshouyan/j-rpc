package com.jing.test.method;

import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.server.method.AsyncMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author jingshouyan
 * #date 2019/3/28 19:24
 */
@Component("asyncTest")
@Slf4j
public class AsyncTest implements AsyncMethod<String, String> {

    @Override
    public Mono<String> action(Token token, String s) {
        return Mono.fromCallable(() -> "abc" + s)
                .subscribeOn(Schedulers.single());
    }
}
