package com.github.jingshouyan.jrpc.client.discover;

import com.github.jingshouyan.jrpc.base.bean.ServerInfo;

/**
 * @author jingshouyan
 * #date 2018/10/26 16:11
 */
public interface ServerInfoListener {

    /**
     * 执行事件
     *
     * @param event      事件
     * @param serverInfo 服务信息
     */
    void handle(DiscoverEvent event, ServerInfo serverInfo);
}
