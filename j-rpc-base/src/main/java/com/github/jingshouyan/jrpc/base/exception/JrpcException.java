package com.github.jingshouyan.jrpc.base.exception;

import lombok.Getter;

/**
 * @author jingshouyan
 * #date 2018/10/16 17:13
 */
public class JrpcException extends RuntimeException{

    @Getter
    private int code;
    @Getter
    private Object data;
    @Getter
    private String detail;

    public JrpcException(int code){
        super("JrpcException:" + code);
        this.code = code;
    }

    public JrpcException(int code, Object data){
        super("JrpcException:" + code);
        this.code = code;
        this.data = data;
    }

    public JrpcException(int code, Object data, String detail){
        super("JrpcException:" + code);
        this.code = code;
        this.data = data;
        this.detail = detail;
    }

    public JrpcException(int code, Throwable cause){
        super(cause);
        this.code = code;
    }
}
