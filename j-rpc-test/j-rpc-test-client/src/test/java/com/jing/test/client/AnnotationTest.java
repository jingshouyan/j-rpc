package com.jing.test.client;

import com.github.jingshouyan.jrpc.base.bean.InterfaceInfo;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.google.common.collect.Lists;
import com.jing.test.client.rpc.TestService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

/**
 * @author jingshouyan
 * #date 2019/9/18 17:43
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
@Slf4j
public class AnnotationTest {
    @Autowired
    private TestService testService;

    @Test
    @SneakyThrows
    public void test() {
        Token token = new Token();
//        testService.traceTest2(token,2);
//
//        Rsp rsp = testService.asyncErr(token,"abc");
//        log.info("1:{}",rsp);
//
//        String str = testService.asyncTest(token,"eeer");
//        log.info("2:{}",str);

        Mono<Object> voidMono = testService.myMethod(token, new Object());
        voidMono.map(x -> 1).subscribe(x -> log.info("3:222"));

        Mono<InterfaceInfo> interfaceInfoMono = testService.getServerInfo(token, new Object());
        Disposable subscribe = interfaceInfoMono.flatMap(x -> Mono.empty()).subscribe(x -> log.info("6:{}", x));
        Mono<Rsp> rspMono = testService.traceTest(token, 2);
        rspMono.subscribe(x -> log.info("4:{}", x));

        Mono<Object> objectMono = testService.testMethod(token, Lists.newArrayList("123", "345", "456"));
        objectMono.subscribe(x -> log.info("5:{}", x));

        Mono.empty().subscribe(x -> log.info("3:222"));
        Thread.sleep(100);


    }

    @Test
    public void traceTest() {
        Token token = new Token();

        Mono<Rsp> mono = testService.traceTest(token, 3);
        mono.block();
    }

    @Test
    public void traceTest2() {
        Token token = new Token();

        testService.traceTest2(token, 3);
    }

    @Test
    public void asyncTest() {
        Token token = new Token();
        testService.asyncTest(token, "123abc");
    }
}