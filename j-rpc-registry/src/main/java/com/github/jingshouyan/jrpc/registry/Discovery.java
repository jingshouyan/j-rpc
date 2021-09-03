package com.github.jingshouyan.jrpc.registry;

/**
 * 发现
 *
 * @author jingshouyan
 * 2020-09-29 10:23
 **/
public interface Discovery {


    /**
     * 注册监听
     *
     * @param listener 节点监听
     */
    void addListener(NodeListener listener);

}
