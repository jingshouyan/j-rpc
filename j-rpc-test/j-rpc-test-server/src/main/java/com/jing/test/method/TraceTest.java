package com.jing.test.method;

import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.client.JrpcClient;
import com.github.jingshouyan.jrpc.client.Request;
import com.github.jingshouyan.jrpc.server.method.Method;
import com.github.jingshouyan.jrpc.starter.server.ServerProperties;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jing.test.rpc.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author jingshouyan
 * 11/15/18 4:08 PM
 */
@Component
public class TraceTest implements Method<Integer, Integer> {
    private static final int LOOP = 2;

    private static final ExecutorService EXEC = new ThreadPoolExecutor(20, 20,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(),
            new ThreadFactoryBuilder().setNameFormat("exec-%d").build());

    @Autowired
    private TestService testService;

    @Override
    public Integer action(Token token, Integer i) {
        if (i != null && i > 0) {
            for (int j = 0; j < LOOP; j++) {
                testService.traceTest(token, i -1).subscribe();
            }
        }
        return i;
    }
}
