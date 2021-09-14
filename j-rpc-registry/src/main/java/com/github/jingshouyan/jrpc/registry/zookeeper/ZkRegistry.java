package com.github.jingshouyan.jrpc.registry.zookeeper;


import com.github.jingshouyan.jrpc.base.info.RegisterInfo;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import com.github.jingshouyan.jrpc.registry.Registry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.StandardCharsets;

/**
 * 服务注册 zookeeper
 *
 * @author jingshouyan
 * 2020-09-17 15:12
 **/
@Slf4j
public class ZkRegistry implements Registry {

    public static final String P = "/";
    public static final String Q = ":";

    private final CuratorFramework client;
    private final String namespace;
    private boolean serving = false;

    public ZkRegistry(CuratorFramework client, String namespace) {
        this.client = client;
        this.namespace = namespace;
    }

    @Override
    public void register(RegisterInfo registerInfo) {
        try {
            serving = true;
            log.debug("register zk starting...");
            String path = zkPath(registerInfo);
            deleteZkNode(registerInfo);
            createZkNode(registerInfo);
            log.debug("serviceInstance:[{}]", registerInfo);

            TreeCache cache = new TreeCache(client, path);
            cache.getListenable().addListener((cf, event) -> {
                if (event.getType() == TreeCacheEvent.Type.NODE_REMOVED
                        && path.equals(event.getData().getPath())
                        && serving) {
                    log.warn("node deleted,register again.");
                    createZkNode(registerInfo);
                }
            });
            cache.start();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    serving = false;
                    log.debug("server stop...");
                    cache.close();
                    deleteZkNode(registerInfo);
                    client.close();

                } catch (Exception e) {
                    log.error("delete zk node [{}] error.", path, e);
                }
            }));

        } catch (Exception e) {
            log.error("register zk error.", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregister(RegisterInfo serviceInfo) {
        serving = false;
        deleteZkNode(serviceInfo);
    }


    private String zkPath(RegisterInfo registerInfo) {
        return namespace + P
                + registerInfo.getName() + P
                + registerInfo.getVersion() + P
                + registerInfo.getIp() + Q
                + registerInfo.getPort() + Q
                + registerInfo.getSsid();
    }

    @SneakyThrows
    private String createZkNode(RegisterInfo info) {
        String path = zkPath(info);
        String data = JsonUtil.toJsonString(info);
        String realPath = client.create()
                .creatingParentContainersIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path, data.getBytes(StandardCharsets.UTF_8));
        log.debug("create zk node :{},data:{}", path, data);
        return realPath;
    }

    @SneakyThrows
    private void deleteZkNode(RegisterInfo info) {
        String path = zkPath(info);
        Stat stat = client.checkExists().forPath(path);
        if (stat != null) {
            client.delete().forPath(path);
            log.debug("delete zk node [{}] .", path);
        }
    }


}
