package com.github.jingshouyan.jrpc.base;

/**
 * @author jingshouyan
 * #date 2019/6/28 19:00
 */

public class StringTest {
    public static void main(String[] args) {
        String a = "abcdddeff";
        String b = a.replace('\\', ' ');
        System.out.println(a == b);
    }
}
