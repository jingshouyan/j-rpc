package com.github.jingshouyan.jrpc.registry;

import com.github.jingshouyan.jrpc.base.bean.Router;
import com.github.jingshouyan.jrpc.registry.node.NodeGroup;
import com.github.jingshouyan.jrpc.registry.node.SvrNode;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 服务节点管理
 *
 * @author jingshouyan
 * 2021-06-01 14:12
 **/
public class NodeManager implements NodeListener {

    private static final ExecutorService EXEC = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("node-event").build());
    private final List<NodeListener> listeners = Lists.newArrayList();
    private final Map<String, NodeGroup> groupMap = Maps.newConcurrentMap();


    public static final int CONSISTENT_HASH_REPLICAS = 3;

    public NodeManager(Discovery discovery) {
        discovery.addListener(this);
    }

    public SvrNode getNode(Router router) {
        NodeGroup group = getNodeGroup(router.getServer(),router.getVersion());
        if(!Strings.isNullOrEmpty(router.getInstance())){
            return group.getBySsid(router.getInstance());
        }
        return group.get();
    }

    public void addListener(NodeListener listener) {
        listeners.add(listener);
    }



    @Override
    public void onChange(NodeEvent event, SvrNode node) {
        switch (event) {
            case ADD:
                addNode(node);
                break;
            case REMOVE:
                removeNode(node);
                break;
            default:
        }
        for (NodeListener listener : listeners) {
            EXEC.execute(() -> listener.onChange(event, node));
        }
    }


    /**
     * 添加节点
     *
     * @param node node
     */
    private void addNode(SvrNode node) {
        NodeGroup nodeGroup = getNodeGroup(node.getName(), node.getVersion());
        nodeGroup.add(node);
    }

    /**
     * 移除节点
     *
     * @param node node
     */
    private void removeNode(SvrNode node) {
        NodeGroup nodeGroup = getNodeGroup(node.getName(), node.getVersion());
        nodeGroup.remove(node);

    }

    /**
     * 获取一个服务节点
     *
     * @param name    服务名
     * @param version 版本
     * @return 节点信息
     */
    public SvrNode pickOne(String name, String version) {
        NodeGroup group = getNodeGroup(name, version);
        return group.get();
    }

    private NodeGroup getNodeGroup(String name, String version) {
        String groupKey = groupKey(name, version);
        NodeGroup group = groupMap.get(groupKey);
        if (group != null) {
            return group;
        }
        return groupMap.computeIfAbsent(groupKey, key -> new NodeGroup(key,CONSISTENT_HASH_REPLICAS));
    }


    private String groupKey(String name, String version) {
        return name + "@" + version;
    }

}
