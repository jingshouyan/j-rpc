package com.jing.test.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author jingshouyan
 * #date 2018/11/1 11:39
 */
@Component
@Aspect
@Slf4j(topic = "Trace-Log")
public class TraceAop {

    @Pointcut("bean(rpc)")
    public void aspect() {
    }

    @Around("aspect()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        log.info("start");
        for (int i = 0; i < args.length; i++) {
            log.info("arg.{}===>{}", i, args[i]);
        }
        Object result = joinPoint.proceed();
        log.info("result.===>{}", result);
        log.info("end");
        return result;
    }
}
