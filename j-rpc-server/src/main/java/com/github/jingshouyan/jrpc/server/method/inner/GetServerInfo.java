package com.github.jingshouyan.jrpc.server.method.inner;

import com.github.jingshouyan.jrpc.base.bean.*;
import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.base.util.bean.ClassInfoUtil;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import com.github.jingshouyan.jrpc.server.method.Method;
import com.github.jingshouyan.jrpc.server.method.holder.MethodHolder;
import com.github.jingshouyan.jrpc.server.run.ServeRunner;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jingshouyan
 * #date 2018/10/23 11:42
 */
public class GetServerInfo implements Method<Empty, InterfaceInfo> {

    @Getter@Setter
    private ServerInfo serverInfo;

    public GetServerInfo() {
    }

    @Override
    public InterfaceInfo action(Token token, Empty empty) {
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setCodeInfos(codes());
        interfaceInfo.setMethodInfos(methods());
        interfaceInfo.setServerInfo(serverInfo);
        return interfaceInfo;
    }

    private List<CodeInfo> codes() {
        List<CodeInfo> codes = Lists.newArrayList();
        Code.getCodeMap().forEach((k, v) -> codes.add(new CodeInfo(k, v, null)));
        codes.sort(Comparator.comparingInt(CodeInfo::getCode));
        return codes;
    }

    private List<MethodInfo> methods() {
        return MethodHolder.methodDefinitions()
                .stream()
                .filter(m -> !(m.getMethod() instanceof GetServerInfo))
                .map(m -> {
                    MethodInfo methodInfo = new MethodInfo();
                    methodInfo.setName(m.getMethodName());
                    BeanInfo input = ClassInfoUtil.beanInfo(m.getInputType());
                    BeanInfo output = ClassInfoUtil.beanInfo(m.getOutputType());
                    String rootType = output.getRootType();
                    rootType = "Rsp<" + rootType + ">";
                    TypeInfo rsp = new TypeInfo();
                    rsp.setType(rootType);
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
                    output.setRootType(rootType);
                    output.getTypes().add(0, rsp);
                    methodInfo.setInput(input);
                    methodInfo.setOutput(output);
                    return methodInfo;
                }).collect(Collectors.toList());
    }


    public static void main(String[] args) {
        GetServerInfo getServerInfo = new GetServerInfo();
        MethodHolder.addMethod("ping", new Ping());
        InterfaceInfo serverInfo = getServerInfo.action(new Token(), new Empty());
        System.out.println(JsonUtil.toJsonString(serverInfo));
    }


}
