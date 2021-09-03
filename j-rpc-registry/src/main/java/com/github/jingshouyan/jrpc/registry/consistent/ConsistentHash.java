package com.github.jingshouyan.jrpc.registry.consistent;

import java.util.Collection;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 一致性hash
 *
 * @author jingshouyan
 * 2021-09-03 13:27
 **/
public class ConsistentHash<T> {

    private int replicas;
    private HashFunc hashFunc;
    /**
     * 一致性Hash环
     */
    private final SortedMap<Integer, T> circle = new TreeMap<>();

    /**
     * @param replicas 副本数
     */
    public ConsistentHash(int replicas) {
        new ConsistentHash<>(replicas, Collections.emptyList());
    }

    /**
     * @param replicas 副本数
     * @param nodes    初始节点
     */
    public ConsistentHash(int replicas, Collection<T> nodes) {
        new ConsistentHash<>(replicas, nodes, HashFunc.FNV1_32_HASH);
    }

    /**
     * @param replicas 副本数
     * @param nodes    初始节点
     * @param hashFunc hash算法
     */
    public ConsistentHash(int replicas, Collection<T> nodes, HashFunc hashFunc) {
        this.replicas = replicas;
        this.hashFunc = hashFunc;
        if (nodes != null && !nodes.isEmpty()) {
            for (T node : nodes) {
                add(node);
            }
        }
    }

    /**
     * 增加节点<br>
     * 每增加一个节点，就会在闭环上增加给定复制节点数<br>
     * 例如复制节点数是2，则每调用此方法一次，增加两个虚拟节点，这两个节点指向同一Node
     *
     * @param node 节点对象
     */
    public void add(T node) {
        String key = key(node);
        for (int i = 0; i < replicas; i++) {
            circle.put(hashFunc.hash(key + i), node);
        }
    }

    /**
     * 移除节点的同时移除相应的虚拟节点
     *
     * @param node 节点对象
     */
    public void remove(T node) {
        String key = key(node);
        for (int i = 0; i < replicas; i++) {
            circle.remove(hashFunc.hash(key + i));
        }
    }

    /**
     * 获得一个最近的顺时针节点
     *
     * @param key 为给定键取Hash，取得顺时针方向上最近的一个虚拟节点对应的实际节点
     * @return 节点对象
     */
    public T get(String key) {
        if (circle.isEmpty()) {
            return null;
        }
        int hash = hashFunc.hash(key);
        SortedMap<Integer, T> tailMap = circle.tailMap(hash);
        hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        return circle.get(hash);
    }

    private String key(T node) {
        return node.toString();
    }
}
