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
public class CheckRunningTimeAspect {

    @Around("@annotation(CheckRunningTime)")
    public Object check(final ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String packageName = signature.getDeclaringTypeName();
        String methodName = signature.getName();
        String target = packageName + "." + methodName;

        long start = System.currentTimeMillis();
        Object proceed =  pjp.proceed();
        long end = System.currentTimeMillis();

        log.info("[ {} ] {}ms", target, end-start);

        return proceed;
    }
}
