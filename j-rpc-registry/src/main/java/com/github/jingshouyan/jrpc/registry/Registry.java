package com.github.jingshouyan.jrpc.registry;


import com.github.jingshouyan.jrpc.base.info.RegisterInfo;

/**
 * 服务注册
 *
 * @author jingshouyan
 * 2020-09-17 14:56
 **/
public interface Registry {
    /**
     * 服务注册
     *
     * @param registerInfo 服务信息
     */
    void register(RegisterInfo registerInfo);

    /**
     * 服务取消注册
     *
     * @param registerInfo 服务信息
     */
    void unregister(RegisterInfo registerInfo);
}
