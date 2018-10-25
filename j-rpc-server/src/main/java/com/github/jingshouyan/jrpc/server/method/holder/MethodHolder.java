package com.github.jingshouyan.jrpc.server.method.holder;

import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.base.exception.JException;
import com.github.jingshouyan.jrpc.server.method.GetServeInfo;
import com.github.jingshouyan.jrpc.server.method.Method;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author jingshouyan
 * #date 2018/10/22 22:27
 */
public class MethodHolder {
    private static final Map<String,Method> METHOD_MAP = Maps.newConcurrentMap();

    static {
        addMethod("getServeInfo",new GetServeInfo());
    }

    public static void addMethod(String methodName,Method method){
        METHOD_MAP.put(methodName,method);
    }

    public static Method getMethod(String methodName){
        if(methodName == null){
            throw new JException(Code.METHOD_NOT_FOUND);
        }
        Method method = METHOD_MAP.get(methodName);
        if(method == null){
            throw new JException(Code.METHOD_NOT_FOUND);
        }
        return method;
    }

    public static Map<String,Method> getMethodMap() {
        return Maps.newHashMap(METHOD_MAP);
    }
}
