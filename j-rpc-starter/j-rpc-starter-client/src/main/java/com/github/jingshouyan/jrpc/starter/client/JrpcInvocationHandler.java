package com.github.jingshouyan.jrpc.starter.client;

import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.client.JrpcClient;
import com.github.jingshouyan.jrpc.client.Request;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * @author jingshouyan
 * #date 2019/9/23 20:14
 */

public class JrpcInvocationHandler implements InvocationHandler {

    private JrpcClient client;
    private String server;
    private String version;

    public JrpcInvocationHandler(JrpcClient client, String server, String version) {
        this.client = client;
        this.server = server;
        this.version = version;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Type type = method.getGenericReturnType();
        if (method.getDeclaringClass() != Object.class) {
            ResultType resultType = getResultType(type);
            //实现业务逻辑,比如发起网络连接，执行远程调用，获取到结果，并返回
            System.out.println(method.getName() + " method invoked ! param: " + Arrays.toString(args));
            Token token = (Token) args[0];
            Object paramObj = args[1];
            Mono<Rsp> rspMono = Request.newInstance()
                    .setServer(server)
                    .setMethod(method.getName())
                    .setClient(client)
                    .setVersion(StringUtils.hasText(version) ? version : null)
                    .setToken(token)
                    .setParamObj(paramObj)
                    .asyncSend();
            switch (resultType.type) {
                case VOID:
                    rspMono.subscribe();
                    return null;
                case RSP:
                    return rspMono.block();
                case OBJECT:
                    return rspMono.block().checkSuccess().getByType(resultType.getObjectType());
                case MONO_VOID:
                    return rspMono.flatMap(rsp -> Mono.empty());
                case MONO_RSP:
                    return rspMono;
                case MONO_OBJECT:
                    return rspMono.map(Rsp::checkSuccess)
                            .flatMap(rsp -> {
                                Object data = rsp.getByType(resultType.getObjectType());
                                return Mono.justOrEmpty(data);
                            });
                default:
            }

        }
        return method.invoke(this, args);
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
