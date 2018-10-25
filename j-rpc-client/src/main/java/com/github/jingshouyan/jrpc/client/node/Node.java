package com.github.jingshouyan.jrpc.client.node;

import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jingshouyan
 * @date 2018/4/18 0:39
 */
@Data
public class Node {
    private ServerInfo serverInfo;
    private AtomicInteger count = new AtomicInteger(0);
    private boolean health = true;
}
