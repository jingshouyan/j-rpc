package com.jing.test.method;

import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.server.method.AsyncMethod;
import io.reactivex.Single;
import org.springframework.stereotype.Component;

/**
 * @author jingshouyan
 * #date 2019/3/29 20:21
 */
@Component("asyncErr")
public class AsyncErr implements AsyncMethod<String, String> {

    @Override
    public Single<String> action(Token token, String s) {
        return Single.fromCallable(() -> {
            throw new RuntimeException("3123123");
        });
    }
}