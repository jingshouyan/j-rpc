package com.github.jingshouyan.jrpc.client.discover.selector.impl;

import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.client.discover.selector.Selector;
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

    public static final int INT_MAX = Integer.MAX_VALUE - 1024 * 1024;

    private final Map<String, AtomicInteger> iMap = Maps.newConcurrentMap();

    private AtomicInteger getAtc(String key) {
        return iMap.computeIfAbsent(key, k -> new AtomicInteger(0));
    }

    @Override
    public List<ServerInfo> versionFilter(List<ServerInfo> infos, String version) {
        return infos.stream()
                .filter(i -> version.equalsIgnoreCase(i.getVersion()))
                .collect(Collectors.toList());
    }

    @Override
    public ServerInfo pickOne(List<ServerInfo> infos) {
        if (infos.size() == 1) {
            return infos.get(0);
        }
        String key = infos.get(0).getName();
        AtomicInteger atc = getAtc(key);
        int i = atc.getAndIncrement();
        int pick = i % infos.size();
        if (i > INT_MAX) {
            atc.set(0);
        }
        return infos.get(pick);
    }
}
