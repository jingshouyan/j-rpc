package com.github.jingshouyan.jrpc.server;

import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import com.github.jingshouyan.jrpc.server.bean.ClassInfo;
import com.github.jingshouyan.jrpc.server.util.bean.ClassInfoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author jingshouyan
 * #date 2018/10/24 11:08
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class TypeTest2 {


    @Test
    public void test(){
        ClassInfo classInfo = ClassInfoUtil.getClassInfo(TestBean1.class,6);
        String json = JsonUtil.toJsonString(classInfo);
        System.out.println(json);
    }


}
