package com.github.jingshouyan.jrpc.client.discover.selector;

import com.github.jingshouyan.jrpc.base.bean.ServerInfo;

import java.util.List;

/**
 * @author jingshouyan
 * #date 2018/10/25 23:18
 */
public interface Selector {
    /**
     * 版本过滤
     * @param infos 服务信息列表
     * @param version 版本
     * @return 过滤后的列表
     */
    List<ServerInfo> versionFilter(List<ServerInfo> infos,String version);

    /**
     * 取一个
     * @param infos 服务信息列表
     * @return 一条
     */
    ServerInfo pickOne(List<ServerInfo> infos);
}
