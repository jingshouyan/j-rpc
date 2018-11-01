package com.jing.test.client;

import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.client.JrpcClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.stream.IntStream;

/**
 * @author jingshouyan
 * #date 2018/10/26 12:03
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
public class ClientTest {
    @Resource
    private JrpcClient jrpcClient;

    @Test
    public void test() {
        IntStream.rangeClosed(0,1000)
//                .parallel()
                .forEach(i -> {
            Rsp rsp = jrpcClient.newRequest().setServer("test").setMethod("myMethod").send();
            System.out.println(rsp);
        });




    }
}