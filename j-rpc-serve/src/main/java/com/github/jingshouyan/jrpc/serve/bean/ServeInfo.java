package com.github.jingshouyan.jrpc.serve.bean;

import com.github.jingshouyan.jrpc.base.bean.ServiceInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * @author jingshouyan
 * #date 2018/10/23 11:24
 */
@Getter
@Setter
@ToString
public class ServeInfo {
    private ServiceInfo serviceInfo;
    private List<MethodInfo> methodInfos;
    private List<CodeInfo> codeInfos;
}
