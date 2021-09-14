package com.github.jingshouyan.jrpc.registry.profile;

import com.github.jingshouyan.jrpc.base.info.ConnectInfo;
import com.github.jingshouyan.jrpc.base.info.RegisterInfo;
import com.github.jingshouyan.jrpc.registry.Discovery;
import com.github.jingshouyan.jrpc.registry.NodeEvent;
import com.github.jingshouyan.jrpc.registry.NodeListener;
import com.github.jingshouyan.jrpc.registry.node.SvrNode;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * 基于配置的发现模块
 *
 * @author jingshouyan
 * 2021-09-14 21:34
 **/
@AllArgsConstructor
public class ProfileDiscovery implements Discovery {

    private final List<RegisterInfo> registers;

    @Override
    public void addListener(NodeListener listener) {
        registers.stream()
                .map(r -> {
                    SvrNode node = new SvrNode();
                    node.setKey(UUID.randomUUID().toString());
                    node.setName(r.getName());
                    node.setVersion(r.getVersion());
                    node.setWeight(r.getWeight());
                    node.setSsid(r.getSsid());
                    ConnectInfo connectInfo = new ConnectInfo();
                    connectInfo.setProtocol(r.getProtocol());
                    connectInfo.setHost(r.getIp());
                    connectInfo.setPort(r.getPort());
                    node.setConnectInfo(connectInfo);

                    return node;
                })
                .forEach(node -> listener.onChange(NodeEvent.ADD, node));
    }
}
