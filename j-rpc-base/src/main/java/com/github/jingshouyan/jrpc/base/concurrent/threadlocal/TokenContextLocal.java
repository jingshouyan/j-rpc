package com.github.jingshouyan.jrpc.base.concurrent.threadlocal;

import com.github.jingshouyan.jrpc.base.bean.Token;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author jingshouyan
 * #date 2018/12/24 17:49
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenContextLocal {
    private static final ThreadLocal<Token> CURRENT_LOCAL = new ThreadLocal<>();

    private static final TokenContextLocal INSTANCE = new TokenContextLocal();

    public static TokenContextLocal getInstance(){
        return INSTANCE;
    }

    public void set(Token token) {
        CURRENT_LOCAL.set(token);
    }

    public Token get(){
        return CURRENT_LOCAL.get();
    }

    public void remove(){
        CURRENT_LOCAL.remove();
    }
}
