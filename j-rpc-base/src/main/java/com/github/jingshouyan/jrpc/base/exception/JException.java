package com.github.jingshouyan.jrpc.base.exception;

import lombok.Getter;

/**
 * @author jingshouyan
 * #date 2018/10/16 17:13
 */
public class JException extends RuntimeException{

    @Getter
    private int code;
    @Getter
    private Object data;

    public JException(int code){
        this.code = code;
    }

    public JException(int code, Object data){
        this.code = code;
        this.data = data;
    }

    public JException(int code, String message){
        super(message);
        this.code = code;
    }

    public JException(int code, Throwable cause){
        super(cause);
        this.code = code;
    }
}
