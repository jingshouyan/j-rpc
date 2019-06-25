package com.github.jingshouyan.jrpc.base.bean;

import lombok.Data;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jingshouyan
 * #date 2018/4/19 19:21
 */
@Data
public class Trace {
    private String traceId;
    private AtomicInteger num = new AtomicInteger(0);

    public String newTraceId() {
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
        }
        return traceId + "." + num.getAndIncrement();
    }
}
