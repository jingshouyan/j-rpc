package com.github.jingshouyan.jrpc.client.discover.selector.impl;

import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.client.discover.selector.Selector;
import com.github.jingshouyan.jrpc.client.node.Node;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author jingshouyan
 * #date 2018/10/25 23:18
 */
public class SimpleSelector implements Selector {

    public static final int INT_MAX = Integer.MAX_VALUE >> 1;

    private final Map<String, AtomicInteger> iMap = Maps.newConcurrentMap();

    private AtomicInteger getAtc(String key) {
        return iMap.computeIfAbsent(key, k -> new AtomicInteger(0));
    }

    @Override
    public List<Node> versionFilter(List<Node> infos, String version) {
        return infos.stream()
                .filter(i -> version.equalsIgnoreCase(i.getServerInfo().getVersion()))
                .collect(Collectors.toList());
    }

    @Override
    public Node pickOne(List<Node> nodes) {
        if (nodes.size() == 1) {
            return nodes.get(0);
        }
        String key = nodes.get(0).getServerInfo().getName();
        AtomicInteger atc = getAtc(key);
        int i = atc.getAndIncrement();
        int pick = i % nodes.size();
        if (i > INT_MAX) {
            atc.set(0);
        }
        return nodes.get(pick);
    }
}
