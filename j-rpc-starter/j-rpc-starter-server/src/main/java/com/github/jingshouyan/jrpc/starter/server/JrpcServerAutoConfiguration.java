package com.github.jingshouyan.jrpc.starter.server;

import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.server.method.BaseMethod;
import com.github.jingshouyan.jrpc.server.method.handler.ServerActionHandler;
import com.github.jingshouyan.jrpc.server.method.holder.MethodHolder;
import com.github.jingshouyan.jrpc.server.method.inner.GetServerInfo;
import com.github.jingshouyan.jrpc.server.method.inner.Ping;
import com.github.jingshouyan.jrpc.server.run.ServeRunner;
import com.github.jingshouyan.jrpc.server.service.Rpc;
import com.github.jingshouyan.jrpc.server.service.impl.RpcImpl;
import com.github.jingshouyan.jrpc.server.thrift.server.register.Register;
import com.github.jingshouyan.jrpc.server.thrift.server.register.ZkRegister;
import com.github.jingshouyan.jrpc.server.util.MonitorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author jingshouyan
 * #date 2018/10/25 15:15
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({ServerProperties.class})
@Order(1)
public class JrpcServerAutoConfiguration implements ApplicationRunner {

    @Resource
    private ServerProperties properties;
    @Resource
    private InetUtilsProperties inetUtilsProperties;

    @Resource
    private ApplicationContext ctx;

    @Bean
    @ConditionalOnMissingBean(GetServerInfo.class)
    public GetServerInfo getServerInfo(ServerInfo serverInfo) {
        GetServerInfo getServerInfo = new GetServerInfo();
        getServerInfo.setServerInfo(serverInfo);
        return getServerInfo;
    }

    @Bean
    @ConditionalOnMissingBean(Ping.class)
    public Ping ping() {
        return new Ping();
    }

    @Bean
    @ConditionalOnMissingBean(ServerActionHandler.class)
    public ServerActionHandler serverActionHandler() {
        return new ServerActionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(Rpc.class)
    public Rpc rpc(ServerActionHandler serverActionHandler) {
        return new RpcImpl(serverActionHandler);
    }

    @Bean
    @ConditionalOnMissingBean(ServerInfo.class)
    public ServerInfo serverInfo(){
        LocalDateTime now = LocalDateTime.now();
        String nowStr = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ServerInfo info = new ServerInfo();
        info.setZkHost(properties.getZkHost());
        info.setZkRoot(properties.getZkRoot());
        info.setName(properties.getName());
        info.setVersion(properties.getVersion());
        if (StringUtils.isEmpty(properties.getHost())) {
            InetUtils inetUtils = new InetUtils(inetUtilsProperties);
            InetAddress inetAddress = inetUtils.findFirstNonLoopbackAddress();
            info.setHost(inetAddress.getHostAddress());
        } else {
            info.setHost(properties.getHost());
        }
        info.setPort(properties.getPort());
        info.setStartAt(nowStr);
        info.setTimeout(properties.getTimeout());
        info.setMaxReadBufferBytes(properties.getMaxReadBufferBytes());
        info.setUpdatedAt("");
        info.setSelector(properties.getSelector());
        info.setWorker(properties.getWorker());
        info.setMonitorInfo(MonitorUtil.monitor());
        return info;
    }

    @Bean
    @ConditionalOnMissingBean(Register.class)
    public Register register() {
        return new ZkRegister();
    }

    @Bean
    @ConditionalOnMissingBean(ServeRunner.class)
    public ServeRunner serveRunner(Rpc rpc,Register register,ServerInfo serverInfo){
        ServeRunner serveRunner = new ServeRunner();
        serveRunner.setIface(rpc).setRegister(register).setServerInfo(serverInfo);
        return serveRunner;
    }

    @Override
    public void run(ApplicationArguments args) {
        addMethod();
        if (properties.isRegister()) {
            ctx.getBean(ServeRunner.class).start();
        }
    }

    private void addMethod() {
        ctx.getBeansOfType(BaseMethod.class).forEach(MethodHolder::addMethod);
    }

}
