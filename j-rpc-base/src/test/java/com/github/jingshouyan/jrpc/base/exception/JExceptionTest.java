package com.github.jingshouyan.jrpc.base.exception;

import org.junit.Test;


/**
 * @author jingshouyan
 * #date 2018/11/6 20:48
 */

public class JExceptionTest {

    @Test
    public void test(){
        Object a = "123";
        String b = "abc";
        JException e = new JException(1,a);
        JException e2 = new JException(1,b);
        System.out.println(e);

    }
}
