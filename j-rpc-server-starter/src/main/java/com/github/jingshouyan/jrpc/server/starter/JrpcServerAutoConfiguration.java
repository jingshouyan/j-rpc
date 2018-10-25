package com.github.jingshouyan.jrpc.server.starter;
import com.github.jingshouyan.jrpc.base.bean.MonitorInfo;

import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.server.method.GetServeInfo;
import com.github.jingshouyan.jrpc.server.method.Method;
import com.github.jingshouyan.jrpc.server.method.holder.MethodHolder;
import com.github.jingshouyan.jrpc.server.run.ServeRunner;
import com.github.jingshouyan.jrpc.server.service.Rpc;
import com.github.jingshouyan.jrpc.server.service.impl.RpcImpl;
import com.github.jingshouyan.jrpc.server.util.MonitorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
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

    @Resource
    private ApplicationContext ctx;

    @Bean
    @ConditionalOnMissingBean(GetServeInfo.class)
    public GetServeInfo getServeInfo(){
        return new GetServeInfo();
    }

    @Bean
    @ConditionalOnMissingBean(Rpc.class)
    public Rpc rpc(){
        Rpc rpc  = new RpcImpl();
        return rpc;
    }

    @PostConstruct
    public void init(){
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

        System.setProperty("SERVER_NAME",info.getName());
        System.setProperty("LOG_ROOT_PATH",info.getLogRootPath());
        System.setProperty("LOG_LEVEL",info.getLogLevel());
        System.setProperty("LOG_REF",info.getLogRef());
        System.setProperty("SERVER_INSTANCE",info.key());

        ServeRunner.getInstance().setServerInfo(info).setIface(ctx.getBean(Rpc.class)).start();
        ctx.getBeansOfType(Method.class).forEach(MethodHolder::addMethod);
    }



    @Override
    public void destroy() {

    }
}
