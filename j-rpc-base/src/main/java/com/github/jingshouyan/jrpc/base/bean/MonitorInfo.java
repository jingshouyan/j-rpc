package com.github.jingshouyan.jrpc.base.bean;

import lombok.Data;

/**
 * @author jingshouyan
 * #date 2018/4/23 17:20
 */
@Data
public class MonitorInfo {
    /**
     * 总请求次数.
     */
    private long totalRequest;

    /**
     * 总请求耗时.
     */
    private long totalCost;

    /**
     * 可使用内存.
     */
    private long totalMemory;

    /**
     * 剩余内存.
     */
    private long freeMemory;

    /**
     * 最大可使用内存.
     */
    private long maxMemory;

    /**
     * 操作系统.
     */
    private String osName;

    /**
     * 总的物理内存.
     */
    private long totalMemorySize;

    /**
     * 剩余的物理内存.
     */
    private long freePhysicalMemorySize;

    /**
     * 已使用的物理内存.
     */
    private long usedMemory;

    /**
     * 线程总数.
     */
    private int totalThread;

    /**
     * cpu使用率.
     */
    private double cpuRatio;
}
