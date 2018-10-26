package com.github.jingshouyan.jrpc.server;

import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import com.github.jingshouyan.jrpc.server.method.GetServerInfo;
import com.google.common.collect.Lists;
import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author jingshouyan
 * #date 2018/10/23 21:11
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class TypeTest {
    public static void main(String[] args) {
        t1();
        Field[] _fields = TestBean1.class.getDeclaredFields();
        List<Field> fields = Lists.newArrayList(_fields);
//        for (Field field: fields) {
//            Type type = field.getGenericType();
//            Class clazz = field.getType();
//            Class clazz1 = field.getDeclaringClass();
//            JavaType javaType =
//            System.out.println(field);
//        }
        TestMethod testMethod = new TestMethod();
//        Type type = testMethod.getOutputType();
//        Object obj = JsonUtil.toBean("{}",type);
        System.out.println(1);
    }

    @Test
    public  void t2(){
        A a = new A();
        B b = new B();
        a.setName("a");
        a.setB(b);
        b.setA(a);
        b.setName("b");
        System.out.println(JsonUtil.toJsonString(a));
    }

    @Data
    public static class A {
        private String name;
        private B b;
    }

    @Data
    public static class B {
        private String name;
        private A a;
    }

    public static void t1(){
        TestMethod testMethod = new TestMethod();
        GetServerInfo GetServerInfo = new GetServerInfo();

//
//        System.out.println(obj);
        Type type1 = testMethod.getInputType();
        System.out.println(type1);
        Object obj = JsonUtil.toBean("[123]",type1);
        Type type2 = testMethod.getOutputType();
        System.out.println(type2);
        Object obj2 = JsonUtil.toBean("{}",type2);
//        TestBean2<CodeInfo> testBean = new TestBean2<>();
//        testBean.setTest("test");
//        testBean.setData(Lists.newArrayList(new CodeInfo(1,"success")));
//        String json = JsonUtil.toJsonString(testBean);
//        Object obj2 = JsonUtil.toBean(json,type2);


        Type type3 = GetServerInfo.getInputType();
        System.out.println(type1);
        Type type4 = GetServerInfo.getOutputType();
        System.out.println(type2);
//        Class c = testBean.getClass();
//        TypeInfo typeInfo = BeanUtil.getTypeInfo(type,3);
//        System.out.println(JsonUtil.toJsonString(typeInfo));
//        MethodHolder.addMethod("test",testMethod);
//
//        GetServerInfo GetServerInfo = new GetServerInfo();
//        InterfaceInfo serveInfo = GetServerInfo.action(new Empty());
//
//        System.out.println(JsonUtil.toJsonString(serveInfo));
    }
}
