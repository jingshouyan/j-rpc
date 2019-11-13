package com.github.jingshouyan.jrpc.server.method.holder;

import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.base.exception.JrpcException;
import com.github.jingshouyan.jrpc.server.method.BaseMethod;
import com.github.jingshouyan.jrpc.server.method.MethodDefinition;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;

/**
 * @author jingshouyan
 * #date 2018/10/22 22:27
 */
@Slf4j
public class MethodHolder {
    private static final Map<String, MethodDefinition> METHOD_MAP = Maps.newConcurrentMap();

    public static void addMethod(String methodName, BaseMethod method) {
        log.debug("add method: {} ===> {}", methodName, method);
        MethodDefinition methodDefinition = new MethodDefinition();
        methodDefinition.setMethodName(methodName);
        methodDefinition.setInputType(method.getInputType());
        methodDefinition.setOutputType(method.getOutputType());
        methodDefinition.setMethod(method);
        METHOD_MAP.put(methodName, methodDefinition);
    }

    public static MethodDefinition getMethod(String methodName) {
        if (methodName == null) {
            throw new JrpcException(Code.METHOD_NOT_FOUND);
        }
        MethodDefinition methodDefinition = METHOD_MAP.get(methodName);
        if (methodDefinition == null) {
            throw new JrpcException(Code.METHOD_NOT_FOUND);
        }
        return methodDefinition;
    }

    public static Map<String, MethodDefinition> getMethodMap() {
        return Maps.newHashMap(METHOD_MAP);
    }

    public static Collection<MethodDefinition> methodDefinitions() {
        return METHOD_MAP.values();
    }
}
