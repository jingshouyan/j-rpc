package com.github.jingshouyan.jrpc.server.method;

import com.github.jingshouyan.jrpc.base.bean.Token;
import io.reactivex.Single;

/**
 * @author jingshouyan
 * 11/29/18 5:26 PM
 */
public interface AsyncMethod<T, R> extends BaseMethod<T, R> {

    /**
     * 执行业务
     *
     * @param token 用户信息
     * @param t     入参
     * @return 执行结果
     */
    Single<R> action(Token token, T t);
}
