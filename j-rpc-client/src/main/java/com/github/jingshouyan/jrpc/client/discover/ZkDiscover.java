package com.github.jingshouyan.jrpc.client.discover;

import com.github.jingshouyan.jrpc.base.bean.Router;
import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.base.exception.JrpcException;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import com.github.jingshouyan.jrpc.base.util.zk.ZkUtil;
import com.github.jingshouyan.jrpc.client.config.ClientConfig;
import com.github.jingshouyan.jrpc.client.discover.selector.Selector;
import com.github.jingshouyan.jrpc.client.discover.selector.impl.SimpleSelector;
import com.github.jingshouyan.jrpc.client.node.Node;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author jingshouyan
 * #date 2018/10/25 20:56
 */
@Slf4j
public class ZkDiscover {

    private static final long LATCH_TIMEOUT = 3000;

    private final Map<String, List<Node>> map = Maps.newConcurrentMap();
    private final CountDownLatch latch = new CountDownLatch(1);
    private String zkHost;
    private String zkRoot;
    GenericObjectPoolConfig poolConfig;
    private Selector selector = new SimpleSelector();

    private final List<ServerInfoListener> listeners = Lists.newArrayList();

    public void addListener(ServerInfoListener listener) {
        listeners.add(listener);
    }


    private CuratorFramework client;

    @SneakyThrows
    public ZkDiscover(ClientConfig config) {
        zkHost = config.getZkHost();
        zkRoot = config.getZkRoot();
        poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMinIdle(config.getPoolMinIdle());
        poolConfig.setMaxIdle(config.getPoolMaxIdle());
        poolConfig.setMaxTotal(config.getPoolMaxTotal());
        poolConfig.setTestWhileIdle(true);
        poolConfig.setTimeBetweenEvictionRunsMillis(10000);

        client = ZkUtil.getClient(zkHost);
        listen();
        boolean init = latch.await(LATCH_TIMEOUT, TimeUnit.MILLISECONDS);
        if (!init) {
            throw new RuntimeException("zk init timeout in " + LATCH_TIMEOUT + "ms");
        }
    }

    public Map<String, List<Node>> nodeMap() {
        return map;
    }

    public Node getNode(Router router) {
        try {
            List<Node> infos = map.get(router.getServer());
            if (infos == null || infos.isEmpty()) {
                throw new JrpcException(Code.SERVER_NOT_FOUND);
            }
            if (router.getInstance() != null) {
                return infos.stream().filter(i -> i.getServerInfo().getInstance().equals(router.getInstance()))
                        .findFirst().orElseThrow(() -> new JrpcException(Code.INSTANCE_NOT_FUND));
            }
            if (router.getVersion() != null) {
                infos = selector.versionFilter(infos, router.getVersion());
                if (infos.isEmpty()) {
                    throw new JrpcException(Code.SERVER_NOT_FOUND);
                }
            }
            return selector.pickOne(infos);
        } catch (JrpcException e) {
            throw e;
        } catch (Exception e) {
            throw new JrpcException(Code.GET_SERVER_ADDRESS_TIMEOUT, e);
        }
    }


    private void listen() {
        try {
            TreeCache cache = new TreeCache(client, zkRoot);
            cache.getListenable().addListener((cl, event) -> {
                try {
                    String type = event.getType().name();
                    String path = null;
                    String data = null;
                    ChildData childData = event.getData();
                    if (childData != null) {
                        path = childData.getPath();
                        data = byte2String(childData.getData());
                    }
                    ServerInfo info = toInfo(data);
                    log.debug("tree changed messageType:[{}] path:[{}] data:[{}]", type, path, data);
                    if (null != info || event.getType() == TreeCacheEvent.Type.INITIALIZED || event.getType() == TreeCacheEvent.Type.NODE_REMOVED) {
                        if (event.getType() == TreeCacheEvent.Type.NODE_REMOVED && null == info) {
                            info = new ServerInfo();
                            String[] strings = path.split("/");
                            String instance = strings[strings.length - 1];
                            String name = strings[strings.length - 2];
                            info.setInstance(instance);
                            info.setName(name);
                        }
                        handle(event.getType(), info);
                    }
                } catch (Exception e) {
                    log.error("zk listener error.", e);
                }
            });
            cache.start();
        } catch (Exception e) {
            log.error("zk client error.", e);
        }
    }

    private void handle(TreeCacheEvent.Type type, ServerInfo info) {
        switch (type) {
            case NODE_ADDED:
                add(info);
                triggerEvent(DiscoverEvent.ADD, info);
                break;
            case NODE_UPDATED:
                update(info);
                triggerEvent(DiscoverEvent.UPDATE, info);
                break;
            case NODE_REMOVED:
                remove(info);
                triggerEvent(DiscoverEvent.REMOVE, info);
                break;
            case INITIALIZED:
                latch.countDown();
                break;
            default:
        }
    }

    private void triggerEvent(DiscoverEvent event, ServerInfo info) {
        listeners.parallelStream().forEach(listener -> listener.handle(event, info));
    }

    private void add(ServerInfo info) {
        map.compute(info.getName(), (name, nodes) -> {
            if (nodes == null) {
                nodes = Lists.newArrayList();
            }
            Node node = new Node(info, poolConfig);
            nodes.add(node);
            return nodes;
        });
    }

    private void update(ServerInfo info) {
        List<Node> list = map.get(info.getName());
        if (null != list) {
            list.stream().filter(i -> i.getServerInfo().getInstance().equals(info.getInstance()))
                    .forEach(i -> i.getServerInfo().update(info));
        }
    }

    private void remove(ServerInfo info) {
        List<Node> list = map.get(info.getName());
        if (null != list) {
            Optional<Node> optionalNode = list.stream()
                    .filter(node -> node.getServerInfo().getInstance().equals(info.getInstance()))
                    .findFirst();
            if (optionalNode.isPresent()) {
                Node node = optionalNode.get();
                list.remove(node);
                node.close();
            }
        }
    }


    private static ServerInfo toInfo(String data) {
        ServerInfo info = null;
        if (null != data && !"".equals(data)) {
            try {
                info = JsonUtil.toBean(data, ServerInfo.class);
            } catch (Exception e) {
                log.warn("data:[{}] convert to ServiceInfo error", data, e);
            }
        }
        return info;
    }

    @SneakyThrows
    private static String byte2String(byte[] b) {
        if (b == null) {
            return null;
        }
        return new String(b, StandardCharsets.UTF_8);
    }

}
