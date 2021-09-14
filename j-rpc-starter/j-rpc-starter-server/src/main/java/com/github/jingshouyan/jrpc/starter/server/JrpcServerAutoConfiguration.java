package com.github.jingshouyan.jrpc.starter.server;

import com.github.jingshouyan.jrpc.base.info.RegisterInfo;
import com.github.jingshouyan.jrpc.base.info.ServiceInfo;
import com.github.jingshouyan.jrpc.registry.Registry;
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
import com.github.jingshouyan.jrpc.starter.registry.RegistryProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.commons.util.InetUtilsProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

/**
 * @author jingshouyan
 * #date 2018/10/25 15:15
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({ServerProperties.class})
@Order(1)
public class JrpcServerAutoConfiguration implements ApplicationRunner {

    public static final int MAX_LOOP = 1000;


    @Resource
    private ServerProperties properties;
    @Resource
    private InetUtilsProperties inetUtilsProperties;

    @Resource
    private ApplicationContext ctx;

    @Bean
    @ConditionalOnMissingBean(GetServerInfo.class)
    public GetServerInfo getServerInfo(RegisterInfo registerInfo) {
        GetServerInfo getServerInfo = new GetServerInfo();
        getServerInfo.setServerInfo(registerInfo);
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

//    @Bean
//    @ConditionalOnMissingBean(ServerInfo.class)
//    public ServerInfo serverInfo(){
//        LocalDateTime now = LocalDateTime.now();
//        String nowStr = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//        ServerInfo info = new ServerInfo();
//        info.setZkHost(properties.getZkHost());
//        info.setZkRoot(properties.getZkRoot());
//        info.setName(properties.getName());
//        info.setVersion(properties.getVersion());
//        if (StringUtils.isEmpty(properties.getHost())) {
//            InetUtils inetUtils = new InetUtils(inetUtilsProperties);
//            InetAddress inetAddress = inetUtils.findFirstNonLoopbackAddress();
//            info.setHost(inetAddress.getHostAddress());
//        } else {
//            info.setHost(properties.getHost());
//        }
//        info.setPort(properties.getPort());
//        info.setStartAt(nowStr);
//        info.setTimeout(properties.getTimeout());
//        info.setMaxReadBufferBytes(properties.getMaxReadBufferBytes());
//        info.setUpdatedAt("");
//        info.setSelector(properties.getSelector());
//        info.setWorker(properties.getWorker());
//        info.setMonitorInfo(MonitorUtil.monitor());
//        return info;
//    }

    @Bean
    @ConditionalOnMissingBean(Register.class)
    public Register register() {
        return new ZkRegister();
    }

    @Bean
    @ConditionalOnMissingBean(ServeRunner.class)
    public ServeRunner serveRunner(Rpc rpc, Register register) {
        ServeRunner serveRunner = new ServeRunner();

        serveRunner.setIface(rpc).setServiceInfo(serviceInfo());
        return serveRunner;
    }

    @Autowired
    private Registry registry;

    @Override
    public void run(ApplicationArguments args) throws InterruptedException {
        addMethod();
        if (properties.isRegister()) {
            ServeRunner runner = ctx.getBean(ServeRunner.class);
            runner.start();
            for (int i = 0; i < MAX_LOOP; i++) {
                Thread.sleep(300);
                if (runner.isServing()) {
                    registry.register(registryInfo());
                    break;
                }
            }
            if (!runner.isServing()) {
                log.error("serve run timeout");
            }
        }
    }

    private void addMethod() {
        ctx.getBeansOfType(BaseMethod.class).forEach(MethodHolder::addMethod);
    }

    @Autowired
    private RegistryProperties registryProperties;

    @Bean
    public RegisterInfo registryInfo() {
        RegisterInfo registerInfo = new RegisterInfo();
        registerInfo.setName(properties.getName());
        registerInfo.setVersion(properties.getVersion());
        registerInfo.setIp(registryProperties.localIp());
        registerInfo.setPort(properties.getPort());
        registerInfo.setNetwork("tcp");
        registerInfo.setProtocol("thrift.binary");
        registerInfo.setWeight(1);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String start = LocalDateTime.now().format(dtf);
        registerInfo.setStartTime(start);
        String ssid = UUID.randomUUID().toString().toLowerCase(Locale.ROOT).replaceAll("-", "");
        registerInfo.setSsid(ssid);
        return registerInfo;
    }

    /**
     * 服务配置信息,启动用
     *
     * @return 服务配置信息
     */
    private ServiceInfo serviceInfo() {
        ServiceInfo serviceInfo = new ServiceInfo();
//        serviceInfo.setIp();
        serviceInfo.setPort(properties.getPort());
        serviceInfo.setNetwork("tcp");
        serviceInfo.setProtocol("thrift.binary");
        serviceInfo.setSelectorThreads(properties.getSelector());
        serviceInfo.setWorkerThreads(properties.getWorker());
        serviceInfo.setMaxReadBufferBytes(properties.getMaxReadBufferBytes());
        return serviceInfo;
    }

}
