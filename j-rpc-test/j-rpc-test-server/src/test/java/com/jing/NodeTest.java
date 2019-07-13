package com.jing;

import com.github.jingshouyan.jrpc.base.bean.MonitorInfo;

import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.client.node.Node;
import lombok.SneakyThrows;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class NodeTest {

    @SneakyThrows
    public static void main(String[] args) {
        node();
        Thread.sleep(20000);
    }

    @SneakyThrows
    private static void node() {
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setZkHost("127.0.0.1:2181");
        serverInfo.setName("test");
        serverInfo.setVersion("v1.0");
        serverInfo.setHost("127.0.01");
        serverInfo.setPort(8999);
        serverInfo.setStartAt("");
        serverInfo.setTimeout(5000);
        serverInfo.setMaxReadBufferBytes(0);
        serverInfo.setUpdatedAt("");
        serverInfo.setMonitorInfo(new MonitorInfo());
        serverInfo.setInstance("abc");
        GenericObjectPoolConfig cfg = new GenericObjectPoolConfig();
        cfg.setMinIdle(10);
        cfg.setMaxIdle(30);
        cfg.setMaxTotal(200);
        cfg.setTestWhileIdle(true);
        cfg.setTimeBetweenEvictionRunsMillis(2000);
        Node node = new Node(serverInfo, cfg);
        node.pool();
        Thread.sleep(5000);
        node.close();
    }
}
