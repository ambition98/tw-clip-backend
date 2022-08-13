package com.isedol_clip_backend.util.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class CheckScheduledAspect {

    @Around("@annotation(com.isedol_clip_backend.util.aop.CheckScheduled)")
    public Object check(final ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String methodName = signature.getName();
        long start = System.currentTimeMillis();

        log.info("[Schedule] Started {}", methodName);
        Object proceed =  pjp.proceed();
        long end = System.currentTimeMillis();

        log.info("[Schedule] Ended {}, RunningTime: {}ms", methodName, end-start);

        return proceed;
    }
}
