package com.test.seckill.common.aop;

import com.google.common.util.concurrent.RateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LimitAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(LimitAspect.class);

    private static RateLimiter rateLimiter = RateLimiter.create(10.0);

    @Pointcut("@annotation(com.test.seckill.common.aop.ServiceLimit)")
    public void limitAspect(){

    }

    @Around("limitAspect()")
    public Object around(ProceedingJoinPoint joinPoint){
        Boolean flag = rateLimiter.tryAcquire();
//        LOGGER.info("当前线程{}，{}",Thread.currentThread(),flag);
        Object obj = null;
        try {
            if(flag){
                obj = joinPoint.proceed();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return obj;
    }
}
