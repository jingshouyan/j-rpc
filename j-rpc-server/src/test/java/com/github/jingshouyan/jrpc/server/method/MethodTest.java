package com.github.jingshouyan.jrpc.server.method;

import com.github.jingshouyan.jrpc.base.bean.Empty;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import com.github.jingshouyan.jrpc.server.TestMethod;
import com.github.jingshouyan.jrpc.server.bean.ServerInfo;
import com.github.jingshouyan.jrpc.server.method.factory.MethodFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author jingshouyan
 * #date 2018/10/24 17:58
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MethodTest {

    @Before
    public void before(){
        MethodFactory.addMethod("test",new TestMethod());
    }

    @Test
    public void getServeInfo(){
        GetServeInfo getServeInfo = new GetServeInfo();
        ServerInfo serverInfo = getServeInfo.action(new Empty());
        String json = JsonUtil.toJsonString(serverInfo);
        System.out.println(json);
    }
}
