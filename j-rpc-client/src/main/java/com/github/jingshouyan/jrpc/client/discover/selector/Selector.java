package com.github.jingshouyan.jrpc.client.discover.selector;

import com.github.jingshouyan.jrpc.base.bean.ServerInfo;

import java.util.List;

/**
 * @author jingshouyan
 * #date 2018/10/25 23:18
 */
public interface Selector {
    List<ServerInfo> versionFilter(List<ServerInfo> infos,String version);

    ServerInfo pickOne(List<ServerInfo> infos);
}
