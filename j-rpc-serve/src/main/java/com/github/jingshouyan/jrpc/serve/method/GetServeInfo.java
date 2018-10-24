package com.github.jingshouyan.jrpc.serve.method;

import com.github.jingshouyan.jrpc.base.bean.Empty;
import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import com.github.jingshouyan.jrpc.serve.bean.ClassInfo;
import com.github.jingshouyan.jrpc.serve.bean.CodeInfo;
import com.github.jingshouyan.jrpc.serve.bean.MethodInfo;
import com.github.jingshouyan.jrpc.serve.bean.ServeInfo;
import com.github.jingshouyan.jrpc.serve.method.factory.MethodFactory;
import com.github.jingshouyan.jrpc.serve.util.bean.ClassInfoUtil;
import com.google.common.collect.Lists;

import java.util.Comparator;
import java.util.List;

/**
 * @author jingshouyan
 * #date 2018/10/23 11:42
 */
public class GetServeInfo implements Method<Empty,ServeInfo> {

    private static final int DEEP = 3;

    @Override
    public ServeInfo action(Empty empty) {
        ServeInfo serveInfo = new ServeInfo();
        serveInfo.setCodeInfos(codes());
        serveInfo.setMethodInfos(methods());
        return serveInfo;
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
        MethodFactory.getMethodMap().forEach((k,v) -> {
            MethodInfo methodInfo = new MethodInfo();
            methodInfo.setName(k);
            ClassInfo input = ClassInfoUtil.getClassInfo(v.getInputType(),DEEP);
            methodInfo.setInput(input);
            ClassInfo output = ClassInfoUtil.getClassInfo(v.getOutputType(),DEEP);
            methodInfo.setOutput(output);
            methods.add(methodInfo);
        });
        return methods;
    }

    public static void main(String[] args) {
        GetServeInfo getServeInfo = new GetServeInfo();
        ServeInfo serveInfo = getServeInfo.action(new Empty());
        System.out.println(JsonUtil.toJsonString(serveInfo));
    }


}
