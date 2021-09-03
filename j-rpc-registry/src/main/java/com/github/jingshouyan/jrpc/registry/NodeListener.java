package com.github.jingshouyan.jrpc.registry;

import com.github.jingshouyan.jrpc.registry.node.SvrNode;

/**
 * node 变更
 *
 * @author jingshouyan
 * 2021-06-01 14:54
 **/
@FunctionalInterface
public interface NodeListener {

    /**
     * 节点变更
     *
     * @param event 变更事件
     * @param node  节点
     */
    void onChange(NodeEvent event, SvrNode node);
}
