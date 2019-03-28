package com.github.jingshouyan.jrpc.server.method;


import com.github.jingshouyan.jrpc.base.bean.Token;

/**
 * @author jingshouyan
 * #date 2018/10/22 16:10
 */
public interface Method<T,R> extends BaseMethod<T,R>{


    /**
     * 执行业务
     * @param token 用户信息
     * @param t 入参
     * @return 执行结果
     */
    R action(Token token, T t);
}
