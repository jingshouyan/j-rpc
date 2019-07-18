package com.github.jingshouyan.jrpc.client.discover.selector;

import com.github.jingshouyan.jrpc.client.node.Node;

import java.util.List;

/**
 * @author jingshouyan
 * #date 2018/10/25 23:18
 */
public interface Selector {
    /**
     * 版本过滤
     *
     * @param infos   服务信息列表
     * @param version 版本
     * @return 过滤后的列表
     */
    List<Node> versionFilter(List<Node> infos, String version);

    /**
     * 取一个
     *
     * @param infos 服务信息列表
     * @return 一条
     */
    Node pickOne(List<Node> infos);
}
