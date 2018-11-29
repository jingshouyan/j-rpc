package com.github.jingshouyan.jrpc.server.starter;

import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.server.method.handler.ServerActionHandler;
import com.github.jingshouyan.jrpc.server.method.inner.GetServerInfo;
import com.github.jingshouyan.jrpc.server.method.Method;
import com.github.jingshouyan.jrpc.server.method.holder.MethodHolder;
import com.github.jingshouyan.jrpc.server.run.ServeRunner;
import com.github.jingshouyan.jrpc.server.service.Rpc;
import com.github.jingshouyan.jrpc.server.service.impl.RpcImpl;
import com.github.jingshouyan.jrpc.server.util.MonitorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author jingshouyan
 * #date 2018/10/25 15:15
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(ServerProperties.class)
@Order(1)
public class JrpcServerAutoConfiguration implements ApplicationRunner {

    @Resource
    private ServerProperties properties;

    @Resource
    private ApplicationContext ctx;

    @Bean
    @ConditionalOnMissingBean(GetServerInfo.class)
    public GetServerInfo getServerInfo(){
        return new GetServerInfo();
    }

    @Bean
    @ConditionalOnMissingBean(ServerActionHandler.class)
    public ServerActionHandler serverActionHandler(){
        return new ServerActionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(Rpc.class)
    public Rpc rpc(ServerActionHandler serverActionHandler){
        Rpc rpc  = new RpcImpl(serverActionHandler);
        return rpc;
    }

    @Override
    public void run(ApplicationArguments args) {
        init();
    }

    private void init(){
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

        ServeRunner.getInstance().setServerInfo(info).setIface(ctx.getBean(Rpc.class)).start();
        ctx.getBeansOfType(Method.class).forEach(MethodHolder::addMethod);
    }

}
