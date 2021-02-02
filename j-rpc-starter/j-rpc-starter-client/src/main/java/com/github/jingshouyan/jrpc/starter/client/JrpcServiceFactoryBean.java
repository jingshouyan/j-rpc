package com.github.jingshouyan.jrpc.starter.client;

import com.github.jingshouyan.jrpc.client.JrpcClient;
import com.github.jingshouyan.jrpc.starter.client.factory.ProxyFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNullApi;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.lang.reflect.InvocationHandler;

/**
 * @author jingshouyan
 * #date 2019/9/17 17:46
 */

public class JrpcServiceFactoryBean implements FactoryBean<Object>, InitializingBean, ApplicationContextAware {

    @Getter
    @Setter
    private Class<?> type;

    private ApplicationContext ctx;
    @Getter
    @Setter
    private String server;
    @Getter
    @Setter
    private String version;
    @Getter
    @Setter
    private String alias;

    @Override
    public void afterPropertiesSet() {
        Assert.hasText(this.server, "Server must be set");
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
        this.ctx = applicationContext;
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }

    @Override
    public Object getObject() {
        return getTarget();
    }

    @SneakyThrows
    private Object getTarget() {
        JrpcClient jrpcClient = ctx.getBean(JrpcClient.class);
        InvocationHandler handler = new JrpcInvocationHandler(jrpcClient, server, version);
        return ProxyFactory.newProxyInstance(type.getClassLoader(),type,handler);
    }

}
