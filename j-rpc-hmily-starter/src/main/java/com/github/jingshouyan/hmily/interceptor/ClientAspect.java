package com.github.jingshouyan.hmily.interceptor;

import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.dromara.hmily.common.bean.context.HmilyTransactionContext;
import org.dromara.hmily.common.constant.CommonConstant;
import org.dromara.hmily.common.enums.HmilyRoleEnum;
import org.dromara.hmily.core.concurrent.threadlocal.HmilyTransactionContextLocal;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author jingshouyan
 * #date 2018/12/26 21:18
 */
@Aspect
@Component
public class ClientAspect {

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
            Object[] args = joinPoint.getArgs();
            Token token = (Token) args[0];
            token.set(CommonConstant.HMILY_TRANSACTION_CONTEXT, JsonUtil.toJsonString(hmilyTransactionContext));
        }
        return joinPoint.proceed();
    }
}
