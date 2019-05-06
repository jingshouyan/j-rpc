package com.jing.test.client;

import com.github.jingshouyan.crud.bean.CreateDTO;
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
        CreateDTO createDTO = new CreateDTO();
        createDTO.setType(CreateDTO.TYPE_SINGLE);
        createDTO.setBean("user");
        createDTO.setData(JsonUtil.toJsonString(u));
        Rsp rsp = Request.newInstance()
                .setClient(jrpcClient)
                .setServer("test")
                .setMethod("create")
                .setParamObj(createDTO)
                .send();
        System.out.println(rsp);
    }

    @Test
    public void multiple(){
        UserBean u = new UserBean();
        u.setName("heehdf地方22");
        CreateDTO createDTO = new CreateDTO();
        createDTO.setType(CreateDTO.TYPE_MULTIPLE);
        createDTO.setBean("user");
        List<UserBean> userBeans = Lists.newArrayList();
        createDTO.setData(JsonUtil.toJsonString(userBeans));
        Rsp rsp = Request.newInstance()
                .setClient(jrpcClient)
                .setServer("test")
                .setMethod("create")
                .setParamObj(createDTO)
                .send();
        System.out.println(rsp);
    }
}
