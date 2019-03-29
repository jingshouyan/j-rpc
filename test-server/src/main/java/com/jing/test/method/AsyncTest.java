package com.jing.test.method;

import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.server.method.AsyncMethod;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author jingshouyan
 * #date 2019/3/28 19:24
 */
@Component("asyncTest")
@Slf4j
public class AsyncTest implements AsyncMethod<String,String> {

    @Override
    public Single<String> action(Token token, String s) {
        return Single.fromCallable(() -> "abc"+s)
                .subscribeOn(Schedulers.newThread());
    }
}
