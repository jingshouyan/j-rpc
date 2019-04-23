package com.github.jingshouyan.jrpc.server.thrift.server.register;

import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import com.github.jingshouyan.jrpc.base.util.zk.ZkUtil;
import com.github.jingshouyan.jrpc.server.util.MonitorUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.thrift.server.TServer;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;


/**
 * @author jingshouyan
 * #date 2018/10/25 13:09
 */
@Slf4j
public class ZkRegister implements Register{

    private static final String DEFAULT_CHARSET = "utf-8";

    @Override
    public void register(TServer tserver, ServerInfo info) {
        while (true) {
            try {
                if(!tserver.isServing()){
                    break;
                }
                log.debug("waiting for server start");
                Thread.sleep(2000);
            }catch (Exception e){}

        }
        CuratorFramework client = ZkUtil.getClient(info.getZkHost());
        try{
            log.debug("register zk starting...");
            String path = fullPath(info);
            deleteZkNode(client,info);
            createZkNode(client,info);
            log.debug("serviceInstance:[{}]", info);

            TreeCache cache = new TreeCache(client, path);
            cache.getListenable().addListener((cf,event)->{
                if(event.getType() == TreeCacheEvent.Type.NODE_REMOVED
                        && path.equals(event.getData().getPath())
                        && tserver.isServing()){
                    log.warn("node deleted,register again.");
                    createZkNode(client,info);
                }
            });
            cache.start();
            Runtime.getRuntime().addShutdownHook(new Thread(()->{
                try{
                    log.debug("server stop...");
                    cache.close();
                    deleteZkNode(client,info);
                    client.close();

                }catch (Exception e){
                    log.error("delete zk node [{}] error.",path,e);
                }
            }));

        }catch (Exception e){
            log.error("register zk error.",e);
            System.exit(-1);
        }
    }

    private String fullPath(ServerInfo serverInfo) {
        String serverNamespace = serverInfo.getZkRoot() + "/" + serverInfo.getName();
        String fullPath = serverNamespace + "/" +serverInfo.getInstance();
        return fullPath;
    }

    @SneakyThrows
    private String createZkNode(CuratorFramework client,ServerInfo info){
        String path = fullPath(info);
        info.setMonitorInfo(MonitorUtil.monitor());
        String data = JsonUtil.toJsonString(info);
        String realPath = client.create().
                creatingParentContainersIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path, data.getBytes(DEFAULT_CHARSET));
        log.debug("create zk node :{},data:{}",path,data);
        return realPath;
    }
    @SneakyThrows
    private void deleteZkNode(CuratorFramework client,ServerInfo info){
        String path = fullPath(info);
        Stat stat = client.checkExists().forPath(path);
        if(stat != null){
            client.delete().forPath(path);
        }
        log.debug("delete zk node [{}] .",path);
    }
}
