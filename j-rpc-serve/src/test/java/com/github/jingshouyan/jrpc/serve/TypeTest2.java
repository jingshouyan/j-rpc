package com.github.jingshouyan.jrpc.serve;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import com.github.jingshouyan.jrpc.serve.bean.ClassInfo;
import com.github.jingshouyan.jrpc.serve.util.bean.ClassInfoUtil;
import com.google.common.collect.Lists;
import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
