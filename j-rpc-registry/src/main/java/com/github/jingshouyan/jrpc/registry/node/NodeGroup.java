package com.github.jingshouyan.jrpc.registry.node;

import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.base.exception.JrpcException;
import com.github.jingshouyan.jrpc.base.info.ConnectInfo;
import com.github.jingshouyan.jrpc.registry.consistent.ConsistentHash;
import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author jingshouyan
 * 2021-09-03 11:06
 **/

public class NodeGroup {

    @Getter
    private final String name;
    @Getter
    private final int replicas;

    private final List<SvrNode> nodes = Lists.newArrayList();

    private volatile ConsistentHash<ConnectInfo> consistentHash;

    public NodeGroup(String name, int replicas) {
        this.name = name;
        this.replicas = replicas;
    }


    /**
     * 添加节点
     *
     * @param node node
     */
    public synchronized void add(SvrNode node) {
        boolean exist = nodes
                .stream()
                .anyMatch(n -> Objects.equals(node.getKey(), n.getKey()));
        if (!exist) {
            nodes.add(node);
            addConn(node.getConnectInfo());
        }

    }

    public synchronized SvrNode get() {
        if (nodes.isEmpty()) {
            throw new JrpcException(Code.SERVER_NOT_FOUND);
        }
        int totalWeight = 0;
        SvrNode tmp = null;
        for (SvrNode svr : nodes) {
            svr.incCurWeight();
            if (tmp == null || tmp.getCurWeight() < svr.getCurWeight()) {
                tmp = svr;
            }
            totalWeight += svr.getCurWeight();
        }
        tmp.decCurWeight(totalWeight);
        return tmp;
    }

    /**
     * 移除节点
     *
     * @param node node
     */
    public synchronized void remove(SvrNode node) {
        nodes.removeIf(n -> Objects.equals(node.getKey(), n.getKey()));
        ConnectInfo connectInfo = node.getConnectInfo();
        boolean exist = nodes.stream()
                .anyMatch(n -> n.getConnectInfo().equals(connectInfo));
        if (!exist) {
            removeConn(connectInfo);
        }
    }

    /**
     * 通过一致性hash获取连接信息
     *
     * @param key key
     * @return conn
     */
    public ConnectInfo consistentHash(String key) {
        if (consistentHash == null) {
            synchronized (this) {
                if (consistentHash == null) {
                    initConsistentHash();
                }
            }
        }
        return consistentHash.get(key);
    }

    /**
     * 初始化一致性hash
     */
    private void initConsistentHash() {
        Set<ConnectInfo> connectInfoSet = new HashSet<>();
        for (SvrNode node : nodes) {
            connectInfoSet.add(node.getConnectInfo());
        }
        consistentHash = new ConsistentHash<>(replicas, connectInfoSet);
    }

    private void addConn(ConnectInfo connectInfo) {
        if (consistentHash != null) {
            consistentHash.add(connectInfo);
        }
    }

    private void removeConn(ConnectInfo connectInfo) {
        if (consistentHash != null) {
            consistentHash.remove(connectInfo);
        }
    }



}
