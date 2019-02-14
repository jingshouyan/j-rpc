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
    @Getter
    private String detail;

    public JException(int code){
        super("JException:" + code);
        this.code = code;
    }

    public JException(int code, Object data){
        super("JException:" + code);
        this.code = code;
        this.data = data;
    }

    public JException(int code,Object data, String detail){
        super("JException:" + code);
        this.code = code;
        this.data = data;
        this.detail = detail;
    }

    public JException(int code, Throwable cause){
        super(cause);
        this.code = code;
    }
}
