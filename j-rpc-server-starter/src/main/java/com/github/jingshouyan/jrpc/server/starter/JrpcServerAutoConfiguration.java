package com.github.jingshouyan.jrpc.server.starter;
import com.github.jingshouyan.jrpc.base.bean.MonitorInfo;

import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.server.run.ServeRunner;
import com.github.jingshouyan.jrpc.server.service.Rpc;
import com.github.jingshouyan.jrpc.server.service.impl.RpcImpl;
import com.github.jingshouyan.jrpc.server.util.MonitorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author jingshouyan
 * #date 2018/10/25 15:15
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(ServerProperties.class)
public class JrpcServerAutoConfiguration implements DisposableBean {

    @Resource
    private ServerProperties properties;

    @Bean
    @ConditionalOnMissingBean(Rpc.class)
    public Rpc rpc(){
        Rpc rpc  = new RpcImpl();
        ServerInfo info = new ServerInfo();
        info.setZkHost(properties.getZkHost());
        info.setZkRoot(properties.getZkRoot());
        info.setName(properties.getName());
        info.setVersion(properties.getVersion());
        info.setHost(properties.getHost());
        info.setPort(properties.getPort());
        info.setStartAt("");
        info.setTimeout(properties.getTimeout());
        info.setMaxReadBufferBytes(properties.getMaxReadBufferBytes());
        info.setUpdatedAt("");
        info.setMonitorInfo(MonitorUtil.monitor());
        info.setLogRootPath(properties.getLogRootPath());
        info.setLogLevel(properties.getLogLevel());
        info.setLogRef(properties.getLogRef());
        ServeRunner.getInstance().setServerInfo(info).setIface(rpc).start();

        return rpc;
    }



    @Override
    public void destroy() {

    }
}
