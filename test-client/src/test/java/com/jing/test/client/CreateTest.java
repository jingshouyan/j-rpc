package com.jing.test.client;

import com.github.jingshouyan.crud.bean.C;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import com.github.jingshouyan.jrpc.client.JrpcClient;
import com.github.jingshouyan.jrpc.client.Request;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jingshouyan
 * 12/4/18 1:59 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
public class CreateTest {

    @Resource
    private JrpcClient jrpcClient;

    @Test
    public void single(){
        UserBean u = new UserBean();
        u.setName("heehdf地方");
        C c = new C();
        c.setType(C.TYPE_SINGLE);
        c.setBean("user");
        c.setData(JsonUtil.toJsonString(u));
        Rsp rsp = Request.newInstance()
                .setClient(jrpcClient)
                .setServer("test")
                .setMethod("create")
                .setParamObj(c)
                .send();
        System.out.println(rsp);
    }

    @Test
    public void multiple(){
        UserBean u = new UserBean();
        u.setName("heehdf地方22");
        C c = new C();
        c.setType(C.TYPE_MULTIPLE);
        c.setBean("user");
        List<UserBean> userBeans = Lists.newArrayList();
        c.setData(JsonUtil.toJsonString(userBeans));
        Rsp rsp = Request.newInstance()
                .setClient(jrpcClient)
                .setServer("test")
                .setMethod("create")
                .setParamObj(c)
                .send();
        System.out.println(rsp);
    }
}
