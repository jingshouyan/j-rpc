package com.github.jingshouyan.jrpc.server.method;

import com.github.jingshouyan.jrpc.base.bean.Token;
import io.reactivex.Single;

public interface AsyncMethod<T,R> extends BaseMethod<T,R> {

    /**
     * 执行业务
     * @param token 用户信息
     * @param t 入参
     * @return 执行结果
     */
    Single<R> action(Token token, T t);
}
