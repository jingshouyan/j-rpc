package com.github.jingshouyan.jrpc.server.starter;

import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.server.method.BaseMethod;
import com.github.jingshouyan.jrpc.server.method.handler.ServerActionHandler;
import com.github.jingshouyan.jrpc.server.method.holder.MethodHolder;
import com.github.jingshouyan.jrpc.server.method.inner.GetServerInfo;
import com.github.jingshouyan.jrpc.server.method.inner.Ping;
import com.github.jingshouyan.jrpc.server.run.ServeRunner;
import com.github.jingshouyan.jrpc.server.service.Rpc;
import com.github.jingshouyan.jrpc.server.service.impl.RpcImpl;
import com.github.jingshouyan.jrpc.server.util.MonitorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
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
    public GetServerInfo getServerInfo() {
        return new GetServerInfo();
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
        Rpc rpc = new RpcImpl(serverActionHandler);
        return rpc;
    }

    @Override
    public void run(ApplicationArguments args) {
        addMethod();
        if (properties.isRegister()) {
            registerZk();
        }
    }

    private void addMethod() {
        ctx.getBeansOfType(BaseMethod.class).forEach(MethodHolder::addMethod);
    }

    private void registerZk() {
        LocalDateTime now = LocalDateTime.now();
        String nowStr = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ServerInfo info = new ServerInfo();
        info.setZkHost(properties.getZkHost());
        info.setZkRoot(properties.getZkRoot());
        info.setName(properties.getName());
        info.setVersion(properties.getVersion());
        if (Strings.isBlank(properties.getHost())) {
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

        ServeRunner.getInstance().setServerInfo(info).setIface(ctx.getBean(Rpc.class)).start();
    }


}
