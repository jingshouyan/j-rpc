package com.github.jingshouyan.jrpc.starter.client;

import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.client.JrpcClient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

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
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.ctx = applicationContext;
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }

    @Override
    public Object getObject() throws Exception {
        return getTarget();
    }

    @SuppressWarnings("unchecked")
    <T> T getTarget() {
        JrpcClient jrpcClient = ctx.getBean(JrpcClient.class);
        InvocationHandler handler = new JrpcInvocationHandler(jrpcClient, server, version);
        return (T) Proxy.newProxyInstance(
                type.getClassLoader(), new Class[]{type}, handler
        );
    }

    private ResultType getResultType(Type type) {
        if (type == void.class || type == Void.class) {
            return new ResultType(TypeEnum.VOID);
        } else if (type == Rsp.class) {
            return new ResultType(TypeEnum.RSP);
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            if (pType.getRawType() == Mono.class) {
                Type type0 = pType.getActualTypeArguments()[0];
                if (type0 == void.class || type0 == Void.class) {
                    return new ResultType(TypeEnum.MONO_VOID);
                } else if (type0 == Rsp.class) {
                    return new ResultType(TypeEnum.MONO_RSP);
                }
                return new ResultType(TypeEnum.MONO_OBJECT, type0);
            }
        }
        return new ResultType(TypeEnum.OBJECT, type);
    }

    @Getter
    @Setter
    private static class ResultType {
        private TypeEnum type;
        private Type objectType;


        ResultType(TypeEnum type) {
            this.type = type;
        }

        ResultType(TypeEnum type, Type objectType) {
            this.type = type;
            this.objectType = objectType;
        }
    }

    private enum TypeEnum {
        /**
         *
         */
        VOID,
        RSP,
        OBJECT,
        MONO_VOID,
        MONO_RSP,
        MONO_OBJECT

    }
}
