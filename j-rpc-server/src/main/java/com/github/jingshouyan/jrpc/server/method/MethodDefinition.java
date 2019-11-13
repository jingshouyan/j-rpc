package com.github.jingshouyan.jrpc.server.method;

import lombok.Data;

import java.lang.reflect.Type;

/**
 * @author jingshouyan
 * #date 2019/11/13 15:42
 */
@Data
public class MethodDefinition {
    private String methodName;
    private Type inputType;
    private Type outputType;
    private BaseMethod method;
}
