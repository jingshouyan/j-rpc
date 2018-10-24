package com.github.jingshouyan.jrpc.serve.method;

import com.github.jingshouyan.jrpc.base.bean.Empty;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import com.github.jingshouyan.jrpc.serve.TestMethod;
import com.github.jingshouyan.jrpc.serve.bean.ServeInfo;
import com.github.jingshouyan.jrpc.serve.method.factory.MethodFactory;
import lombok.ToString;
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
        ServeInfo serveInfo = getServeInfo.action(new Empty());
        String json = JsonUtil.toJsonString(serveInfo);
        System.out.println(json);
    }
}
