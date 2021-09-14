package com.github.jingshouyan.jrpc.registry.node;

import com.github.jingshouyan.jrpc.base.info.ConnectInfo;
import lombok.Data;

/**
 * @author jingshouyan
 * 2021-09-03 13:36
 **/
@Data
public class SvrNode {
    /**
     * node 唯一标识,删除node时使用
     */
    private String key;

    private String name;
    private String version;
    private int weight;
    private String ssid;

    private int curWeight;

    private ConnectInfo connectInfo = new ConnectInfo();

    /**
     * 增加当前权重
     */
    public void incCurWeight() {
        curWeight += weight;
    }

    /**
     * 降低权重,被选中后调用
     *
     * @param total 总权重
     */
    public void decCurWeight(int total) {
        curWeight -= total;
    }


    public void onSuccess() {

    }

    public void onError(Throwable t) {

    }
}
