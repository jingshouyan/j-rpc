package com.jing;

import brave.Tracing;

/**
 * @author jingshouyan
 * #date 2018/10/31 16:29
 */
public class BraveTest {

    public static void main(String[] args) {
        Tracing tracing = Tracing.newBuilder().build();
    }
}
