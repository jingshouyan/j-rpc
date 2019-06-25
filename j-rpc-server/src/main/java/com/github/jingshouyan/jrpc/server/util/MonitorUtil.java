package com.github.jingshouyan.jrpc.server.util;

import com.github.jingshouyan.jrpc.base.bean.MonitorInfo;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author jingshouyan
 * #date 2018/10/25 14:31
 */
public class MonitorUtil {

    private static final OperatingSystemMXBean OS_BEAN = ManagementFactory.getOperatingSystemMXBean();
    private static final MemoryMXBean MENORY_BEAN = ManagementFactory.getMemoryMXBean();

    private static final AtomicLong TOTAL_REQUEST = new AtomicLong(0);
    private static final AtomicLong TOTAL_COST = new AtomicLong(0);

    public static MonitorInfo monitor() {
        MonitorInfo monitorInfo = new MonitorInfo();
        monitorInfo.setTotalRequest(TOTAL_REQUEST.get());
        monitorInfo.setTotalCost(TOTAL_COST.get());
        monitorInfo.setTotalMemory(MENORY_BEAN.getHeapMemoryUsage().getInit());
        monitorInfo.setUsedMemory(MENORY_BEAN.getHeapMemoryUsage().getUsed());
        monitorInfo.setFreeMemory(monitorInfo.getTotalMemory() - monitorInfo.getUsedMemory());
        monitorInfo.setMaxMemory(MENORY_BEAN.getHeapMemoryUsage().getMax());
        monitorInfo.setOsName(OS_BEAN.getName());
        monitorInfo.setTotalThread(ManagementFactory.getThreadMXBean().getThreadCount());
        monitorInfo.setCpuRatio(OS_BEAN.getSystemLoadAverage());
        return monitorInfo;
    }

    private static void updateRequest(long cost) {
        TOTAL_REQUEST.incrementAndGet();
        TOTAL_COST.addAndGet(cost);
    }
}
