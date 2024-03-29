package com.jing.test.client;

import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.client.JrpcClient;
import com.github.jingshouyan.jrpc.client.Request;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * @author jingshouyan
 * #date 2018/10/26 12:03
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
@Slf4j
public class ClientTest {
    @Resource
    private JrpcClient jrpcClient;

    @Test
    public void test2() {
        IdQuery idQuery = new IdQuery();
        idQuery.setName("zhangsan");
        idQuery.setAge(77);
        idQuery.setIds(Lists.newArrayList("123", "345"));

        Token token = new Token();

        Rsp rsp = Request.newInstance()
                .setClient(jrpcClient) //设置发送客户端
                .setServer("test")     //调用的服务名
                .setVersion("1.0")     // 必须要设置版本号
//                .setVersion("2.0")   //服务的版本号,只选择向 2.0 版本的服务发送数据,没找到会有相应的错误码
//                .setInstance("test-111") //服务实例名,多个实例可以指定发送到对应的服务,没找到会有相应的错误码
                .setMethod("getUserInfo") //服务方法名
                .setToken(token) // 设置token ,可选 token 信息
                .setParamObj(idQuery) //请求参数对象,也可以使用 setParamJson 直接设置json字符串
//                .setOneway(true) //是否为 oneway 调用,
                .send() //发送请求,这时已经得到 Rsp 对象
                .checkSuccess(); //检查 返回码,不为 SUCCESS 则抛出异常
        List<UserBean> userBeans = rsp.list(UserBean.class); //rsp中result实际为json字符串.list为将json反序列化为 List对象
        List<UserBean> userBeans1 = rsp.get(List.class, UserBean.class); //也可以使用 get 带泛型的反序列化
    }

    @Test
    public void test() {
        IntStream.rangeClosed(0, 10)
//                .parallel()
                .forEach(i -> {
                    Rsp rsp = Request.newInstance()
                            .setClient(jrpcClient)
                            .setServer("test")
                            .setMethod("traceTest")
                            .setParamObj(3)
                            .send();
                    System.out.println(rsp);
                });
    }

    @Test
    @SneakyThrows
    public void asyncErr() {
        for (int i = 0; i < 1; i++)
            Request.newInstance()
                    .setClient(jrpcClient)
                    .setServer("test")
                    .setMethod("asyncErr")
                    .setParamObj(i)
                    .asyncSend()
                    .subscribe(System.err::println);
//        Thread.sleep(1000);
    }

    @Test
    @SneakyThrows
    public void traceTest2() {
        IntStream.rangeClosed(0, 100)
//                .parallel()
                .forEach(i -> {
                    Request.newInstance()
                            .setClient(jrpcClient)
                            .setServer("test")
                            .setVersion("1.0")
                            .setMethod("traceTest2")
                            .setParamObj(9)
                            .asyncSend()
                            .subscribe(rsp -> {
                                        try {
                                            Thread.sleep(1000);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        log.info("--------->>{}", rsp);
                                    }
                            );
                });

        Thread.sleep(500000000);
    }

    @Test
    public void t2() {
        for (int i = 0; i < 1000; i++)
            Request.newInstance()
                    .setClient(jrpcClient)
                    .setServer("test")
                    .setMethod("traceTest2")
                    .setParamObj(3)
                    .asyncSend()
//                .subscribe();
                    .subscribe(rsp -> {
                        System.err.println(rsp);
                        Request.newInstance()
                                .setClient(jrpcClient)
                                .setServer("test")
                                .setMethod("traceTest")
                                .setParamObj(2)
                                .asyncSend()
                                .subscribe();
                    });

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testForward() throws Exception {
        long start = System.currentTimeMillis();
        int loop = 1;
        AtomicInteger ai = new AtomicInteger();
        AtomicInteger ai2 = new AtomicInteger();
        CountDownLatch cdl = new CountDownLatch(loop);
        for (int i = 0; i < loop; i++) {
            List<String> strings = new ArrayList<>();
            strings.add("" + i);
            Request.newInstance()
                    .setClient(jrpcClient)
                    .setServer("forward")
                    .setVersion("1.0")
                    .setMethod("forwardTest")//      forwardTest: test.testMethod
//                    .setServer("test")
//                    .setMethod("testMethod")
                    .setParamObj(strings)
//                .setOneway(true)
//                    .send();
                    .asyncSend()
                    .subscribe(rsp -> {
                        if (!rsp.success()) {
                            ai.getAndIncrement();
                        }
                        ai2.getAndIncrement();
                        cdl.countDown();
                    });
        }
        cdl.await();
        long end = System.currentTimeMillis();
        log.info("loop[{}] use {} ms,error:{},run:{}", loop, end - start, ai.get(), ai2.get());
    }

    @Test
    public void testMethod() {
        for (int i = 0; i < 1000; i++) {
            List<String> strings = new ArrayList<>();
            strings.add("" + i);
            Request.newInstance()
                    .setClient(jrpcClient)
                    .setServer("test")
                    .setMethod("testMethod")
                    .setParamObj(strings)
//                .setOneway(true)
                    .asyncSend().subscribe(System.err::println);
        }

    }

    @Test
    public void testForward2() {
        for (int i = 0; i < 1000; i++) {
            List<String> strings = new ArrayList<>();
            strings.add("abc");
            strings.add("sdf");
            Rsp rsp = Request.newInstance()
                    .setClient(jrpcClient)
                    .setServer("forward")
                    .setMethod("asyncTest")
                    .setParamObj("asdflaskdj")
//                .setOneway(true)
                    .send();
        }

    }

    @Test
    public void pingTest() {
        ping(123);
        ping("asdfasfd");
        ping(new UserBean());
    }

    private Object ping(Object obj) {
        Object out = Request.newInstance()
                .setClient(jrpcClient)
                .setServer("test")
                .setMethod("ping")
                .setParamObj(obj)
//                .setOneway(true)
                .send().get(Object.class);
        System.out.println(out);
        return out;
    }

}
