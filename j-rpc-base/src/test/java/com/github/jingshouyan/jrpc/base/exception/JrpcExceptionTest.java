package com.github.jingshouyan.jrpc.base.exception;

import org.junit.Test;


/**
 * @author jingshouyan
 * #date 2018/11/6 20:48
 */

public class JrpcExceptionTest {

    @Test
    public void test(){
        Object a = "123";
        String b = "abc";
        JrpcException e = new JrpcException(1,a);
        JrpcException e2 = new JrpcException(1,b);
        System.out.println(e);

    }
}
