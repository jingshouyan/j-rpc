package com.jing.test.method;

import com.github.jingshouyan.jrpc.base.bean.CodeInfo;
import com.github.jingshouyan.jrpc.server.method.Method;
import com.jing.test.bean.TestBean2;
import com.jing.test.bean.TestBean3;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author jingshouyan
 * #date 2018/10/23 20:38
 */
@Component
public class TestMethod implements Method<List<String>,TestBean2<CodeInfo,String,TestBean3>> {

    @Override
    public TestBean2<CodeInfo,String,TestBean3> action(List<String> strings) {
        return null;
    }
}


