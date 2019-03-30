package com.jing.test.method;

import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.client.JrpcClient;
import com.github.jingshouyan.jrpc.client.Request;
import com.github.jingshouyan.jrpc.server.method.AsyncMethod;
import com.github.jingshouyan.jrpc.server.starter.ServerProperties;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jingshouyan
 * 11/15/18 4:08 PM
 */
@Component("traceTest2")
public class TraceTest2 implements AsyncMethod<Integer,Integer> {


    @Autowired
    ServerProperties properties;

    @Autowired
    JrpcClient client;

    @Override
    public Single<Integer> action(Token token, Integer i) {
        return Single.fromCallable(()-> {
            if(i!=null && i>0){
                for (int j = 0; j < 2; j++) {
                    Request.newInstance().setClient(client)
                            .setServer(properties.getName())
                            .setMethod("traceTest2")
                            .setParamObj(i-1)
                            .setOneway(true)
                            .asyncSend().subscribe();
                }
            }
            return i;
        });

    }
}
