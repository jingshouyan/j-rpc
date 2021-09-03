package com.github.jingshouyan.jrpc.registry.zookeeper;


import com.github.jingshouyan.jrpc.base.info.RegisterInfo;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import com.github.jingshouyan.jrpc.registry.Discovery;
import com.github.jingshouyan.jrpc.registry.NodeEvent;
import com.github.jingshouyan.jrpc.registry.NodeListener;
import com.github.jingshouyan.jrpc.registry.node.SvrNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * zookeeper 服务发现
 *
 * @author jingshouyan
 * 2020-10-15 14:47
 **/
@Slf4j
public class ZkDiscovery implements Discovery {

    private CuratorFramework client;
    private String namespace;


    public ZkDiscovery(CuratorFramework client, String namespace) {
        this.client = client;
        this.namespace = namespace;
    }


    private static RegisterInfo toRegisterInfo(String json) {
        RegisterInfo info = null;
        if (json != null && !"".equals(json)) {
            try {
                info = JsonUtil.toBean(json, RegisterInfo.class);
            } catch (Exception e) {
                log.warn("data:[{}] convert to RegisterInfo error", json, e);
            }
        }
        return info;
    }

    private static String byte2String(byte[] b) {
        if (b == null) {
            return null;
        }
        return new String(b, StandardCharsets.UTF_8);
    }


    @Override
    public void addListener(NodeListener listener) {
        try {
            TreeCache cache = new TreeCache(client, namespace);
            CountDownLatch latch = new CountDownLatch(1);
            cache.getListenable().addListener((c, event) -> {
                try {
                    if (Objects.equals(event.getType(), TreeCacheEvent.Type.INITIALIZED)) {
                        latch.countDown();
                        return;
                    }
                    String type = event.getType().name();
                    String path = null;
                    String data = null;
                    ChildData childData = event.getData();
                    if (childData != null) {
                        path = childData.getPath();
                        data = byte2String(childData.getData());
                    }
                    log.info("tree changed messageType:[{}] path:[{}] data:[{}]", type, path, data);
                    RegisterInfo info = toRegisterInfo(data);
                    SvrNode node = new SvrNode();
                    node.setKey(path);
                    if (info != null) {
                        node.setVersion(info.getVersion());
                        node.setName(info.getName());
                        node.setWeight(info.getWeight());
                        node.setSsid(info.getSsid());
                        node.getConnectInfo().setProtocol(info.getProtocol());
                        node.getConnectInfo().setHost(info.getIp());
                        node.getConnectInfo().setPort(info.getPort());
                        switch (event.getType()) {
                            case NODE_ADDED:
                                listener.onChange(NodeEvent.ADD, node);
                                break;
                            case NODE_REMOVED:
                                listener.onChange(NodeEvent.REMOVE, node);
                                break;
                            default:
                        }
                    }
                } catch (Exception e) {
                    log.error("zk listener error.", e);
                }
            });
            cache.start();
            latch.await();
        } catch (Exception e) {
            log.error("zk client error.", e);
        }
    }
}
