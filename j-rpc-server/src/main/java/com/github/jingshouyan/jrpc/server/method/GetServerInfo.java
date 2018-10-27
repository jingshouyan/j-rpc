package com.github.jingshouyan.jrpc.server.method;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.github.jingshouyan.jrpc.base.bean.*;
import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import com.github.jingshouyan.jrpc.server.method.holder.MethodHolder;
import com.github.jingshouyan.jrpc.base.util.bean.ClassInfoUtil;
import com.github.jingshouyan.jrpc.server.run.ServeRunner;
import com.google.common.collect.Lists;

import java.util.Comparator;
import java.util.List;

/**
 * @author jingshouyan
 * #date 2018/10/23 11:42
 */
public class GetServerInfo implements Method<Empty,InterfaceInfo> {

    private static final int DEEP = 5;

    @Override
    public InterfaceInfo action(Empty empty) {
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setCodeInfos(codes());
        interfaceInfo.setMethodInfos(methods());
        interfaceInfo.setServerInfo(ServeRunner.getInstance().getServerInfo());
        return interfaceInfo;
    }

    private List<CodeInfo> codes(){
        List<CodeInfo> codes = Lists.newArrayList();
        Code.getCodeMap().forEach((k,v) -> {
            codes.add(new CodeInfo(k,v));
        });
        codes.sort(Comparator.comparingInt(CodeInfo::getCode));
        return codes;
    }

    private List<MethodInfo> methods(){
        List<MethodInfo> methods = Lists.newArrayList();
        MethodHolder.getMethodMap().forEach((k, v) -> {
            if(!(v instanceof GetServerInfo)) {
                MethodInfo methodInfo = new MethodInfo();
                methodInfo.setName(k);
                BeanInfo input = ClassInfoUtil.beanInfo(v.getInputType());
                BeanInfo output = ClassInfoUtil.beanInfo(v.getOutputType());
                TypeInfo rsp = new TypeInfo();
                rsp.setType("Rsp");
                FieldInfo code = new FieldInfo();
                code.setType("int");
                code.setName("code");
                rsp.getFields().add(code);
                FieldInfo message = new FieldInfo();
                message.setType("String");
                message.setName("message");
                rsp.getFields().add(message);
                FieldInfo data = new FieldInfo();
                data.setType(output.getRootType());
                data.setName("data");
                rsp.getFields().add(data);
                output.setRootType("Rsp");
                output.getTypes().add(0,rsp);
                methodInfo.setInput(input);
                methodInfo.setOutput(output);
                methods.add(methodInfo);
            }
        });
        return methods;
    }



    public static void main(String[] args) {
        GetServerInfo GetServerInfo = new GetServerInfo();
        InterfaceInfo serverInfo = GetServerInfo.action(new Empty());
        System.out.println(JsonUtil.toJsonString(serverInfo));
    }


}
