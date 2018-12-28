package com.github.jingshouyan.hmily.interceptor;

import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.dromara.hmily.annotation.Hmily;
import org.dromara.hmily.common.bean.context.HmilyTransactionContext;
import org.dromara.hmily.common.bean.entity.HmilyInvocation;
import org.dromara.hmily.common.bean.entity.HmilyParticipant;
import org.dromara.hmily.common.constant.CommonConstant;
import org.dromara.hmily.common.enums.HmilyActionEnum;
import org.dromara.hmily.common.enums.HmilyRoleEnum;
import org.dromara.hmily.common.exception.HmilyRuntimeException;
import org.dromara.hmily.core.concurrent.threadlocal.HmilyTransactionContextLocal;
import org.dromara.hmily.core.service.executor.HmilyTransactionExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.spec.RC2ParameterSpec;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author jingshouyan
 * #date 2018/12/26 21:18
 */
@Aspect
@Component
public class ClientAspect {

    @Autowired
    private HmilyTransactionExecutor hmilyTransactionExecutor;

    @Pointcut("bean(jrpcClient) && execution(* *.handle(..))")
    public void aspect() {
    }
    @Around("aspect()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        final HmilyTransactionContext hmilyTransactionContext = HmilyTransactionContextLocal.getInstance().get();
        if (Objects.nonNull(hmilyTransactionContext)) {
            if (hmilyTransactionContext.getRole() == HmilyRoleEnum.LOCAL.getCode()) {
                hmilyTransactionContext.setRole(HmilyRoleEnum.INLINE.getCode());
            }
            Object[] arguments = joinPoint.getArgs();
            Token token = (Token) arguments[0];
            token.set(CommonConstant.HMILY_TRANSACTION_CONTEXT, JsonUtil.toJsonString(hmilyTransactionContext));
            Rsp rsp = (Rsp) joinPoint.proceed();
            if(rsp.success()){
                MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
                Method method = methodSignature.getMethod();
                Class clazz = joinPoint.getTarget().getClass();
                final HmilyParticipant hmilyParticipant = buildParticipant(hmilyTransactionContext, method, clazz, arguments);
                if (hmilyTransactionContext.getRole() == HmilyRoleEnum.INLINE.getCode()) {
                    hmilyTransactionExecutor.registerByNested(hmilyTransactionContext.getTransId(),
                            hmilyParticipant);
                } else {
                    hmilyTransactionExecutor.enlistParticipant(hmilyParticipant);
                }
            }
            return rsp;
        } else {
            return joinPoint.proceed();
        }
    }

    private HmilyParticipant buildParticipant(final HmilyTransactionContext hmilyTransactionContext,
                                              final Method method, final Class clazz,
                                              final Object[] arguments) throws HmilyRuntimeException {

        if (Objects.isNull(hmilyTransactionContext)
                || (HmilyActionEnum.TRYING.getCode() != hmilyTransactionContext.getAction())) {
            return null;
        }
        //获取协调方法
        String confirmMethodName = method.getName();

        String cancelMethodName = method.getName();
        Class[] args = getClazz(arguments);
        HmilyInvocation confirmInvocation = new HmilyInvocation(clazz, confirmMethodName, args, arguments);
        HmilyInvocation cancelInvocation = new HmilyInvocation(clazz, cancelMethodName, args, arguments);
        //封装调用点
        return new HmilyParticipant(hmilyTransactionContext.getTransId(), confirmInvocation, cancelInvocation);
    }

    private Class[] getClazz(Object[] objects){
        Class[] classes = new Class[objects.length];
        for (int i = 0; i < objects.length; i++) {
            classes[i] = objects[i].getClass();
        }
        return classes;
    }
}
