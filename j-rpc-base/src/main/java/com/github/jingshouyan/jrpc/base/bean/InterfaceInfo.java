package com.github.jingshouyan.jrpc.base.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author jingshouyan
 * #date 2018/10/23 11:24
 */
@Getter
@Setter
@ToString
public class InterfaceInfo {
    private com.github.jingshouyan.jrpc.base.bean.ServerInfo serverInfo;
    private List<MethodInfo> methodInfos;
    private List<CodeInfo> codeInfos;
}
