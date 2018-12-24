package com.github.jingshouyan.hmily.interceptor;

import org.aspectj.lang.annotation.Aspect;
import org.dromara.hmily.core.interceptor.AbstractHmilyTransactionAspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @author jingshouyan
 * #date 2018/12/24 17:49
 */
@Aspect
@Component
public class JrpcHmilyTransactionAspect extends AbstractHmilyTransactionAspect implements Ordered {

    @Autowired
    public JrpcHmilyTransactionAspect(final JrpcHmilyTransactionInterceptor jrpcHmilyTransactionInterceptor) {
        this.setHmilyTransactionInterceptor(jrpcHmilyTransactionInterceptor);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
