package com.jing.test.client;

import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.client.JrpcClient;
import com.github.jingshouyan.jrpc.client.Request;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
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
    public void test2(){
        IdQuery idQuery = new IdQuery();
        idQuery.setName("zhangsan");
        idQuery.setAge(77);
        idQuery.setIds(Lists.newArrayList("123","345"));

        Token token = new Token();

        Rsp rsp = Request.newInstance()
                .setClient(jrpcClient) //设置发送客户端
                .setServer("test")     //调用的服务名
//                .setVersion("2.0")   //服务的版本号,只选择向 2.0 版本的服务发送数据,没找到会有相应的错误码
//                .setInstance("test-111") //服务实例名,多个实例可以指定发送到对应的服务,没找到会有相应的错误码
                .setMethod("getUserInfo") //服务方法名
                .setToken(token) // 设置token ,可选 token 信息
                .setParamObj(idQuery) //请求参数对象,也可以使用 setParamJson 直接设置json字符串
//                .setOneway(true) //是否为 oneway 调用,
                .send() //发送请求,这时已经得到 Rsp 对象
                .checkSuccess(); //检查 返回码,不为 SUCCESS 则抛出异常
        List<UserBean> userBeans = rsp.list(UserBean.class); //rsp中result实际为json字符串.list为将json反序列化为 List对象
        List<UserBean> userBeans1 = rsp.get(List.class,UserBean.class); //也可以使用 get 带泛型的反序列化
    }

//    @Test
    public void test() {
        IntStream.rangeClosed(0,0)
//                .parallel()
                .forEach(i -> {
                    Rsp rsp = Request.newInstance()
                            .setClient(jrpcClient)
                            .setServer("test")
                            .setMethod("traceTest")
                            .setParamObj(12)
                            .send();
            System.out.println(rsp);
        });



    }

    @Test
    public void testForward(){
        for (int i = 0; i < 1000; i++) {
            List<String> strings = new ArrayList<>();
            strings.add("abc");
            strings.add("sdf");
            Rsp rsp = Request.newInstance()
                    .setClient(jrpcClient)
                    .setServer("forward")
                    .setMethod("forwardTest")
                    .setParamObj(strings)
//                .setOneway(true)
                    .send();
        }

    }

    @Test
    public void testForward2(){
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


}
