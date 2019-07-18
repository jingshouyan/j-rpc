package com.jing.test.method;

import com.github.jingshouyan.jrpc.base.bean.CodeInfo;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.server.method.Method;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jing.test.bean.TestBean2;
import com.jing.test.bean.TestBean3;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author jingshouyan
 * #date 2018/10/23 20:38
 */
@Component
public class TestMethod implements Method<List<String>, TestBean2<CodeInfo, String, TestBean3>> {

    @Override
    @SneakyThrows
    public TestBean2<CodeInfo, String, TestBean3> action(Token token, List<String> strings) {
//        Random random = new Random();
//        int r = random.nextInt(8);
//        if (r > 4) {
//            TimeUnit.SECONDS.sleep(r);
//        }
        TestBean2 testBean2 = new TestBean2();
        testBean2.setTest(strings.get(0));
        CodeInfo codeInfo = new CodeInfo();
        codeInfo.setCode(100);
        codeInfo.setMessage("1123");
        Map<String, String> map = new HashMap<>(0);
        map.put("abc", "eee");
        map.put("abc2", "eee2");
        Map<String, String> map2 = new HashMap<>(0);
        map2.put("2abc", "eee");
        map2.put("2abc2", "eee2");
        TestBean3 testBean3 = new TestBean3();
        testBean3.setTest("testBean3");

        testBean2.setData(Lists.newArrayList(codeInfo));
        testBean2.setMapList(Lists.newArrayList(map, map2));
        testBean2.setSet(Sets.newHashSet(testBean3));
        testBean2.setT(codeInfo);
        return testBean2;

    }
}


