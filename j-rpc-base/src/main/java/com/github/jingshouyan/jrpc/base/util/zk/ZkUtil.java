package com.github.jingshouyan.jrpc.base.util.zk;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryForever;

import java.util.Map;

/**
 * @author jingshouyan
 * #date 2018/10/21 16:04
 */
@Slf4j
public class ZkUtil {

    private static final Map<String,CuratorFramework> CLIENT_MAP = Maps.newConcurrentMap();

    public static CuratorFramework getClient(String connectString) {
        return CLIENT_MAP.computeIfAbsent(connectString,key ->{
            log.debug("new zk client[{}].",key);
            CuratorFramework client = CuratorFrameworkFactory
                    .builder().connectString(key).canBeReadOnly(true)
                    .retryPolicy(new RetryForever(5000))
                    .build();
            client.start();
            log.debug("zk client[{}] started.",key);
            return client;
        });
    }
}
