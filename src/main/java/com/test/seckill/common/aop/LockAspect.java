package com.test.seckill.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Aspect
@Order(1)
public class LockAspect {
    private static Lock lock = new ReentrantLock(true);

    @Pointcut("@annotation(com.test.seckill.common.aop.ServiceLock)")
    public void lockAspect(){

    }
    @Around("lockAspect()")
    public Object around(ProceedingJoinPoint joinPoint){
        lock.lock();
        Object obj = null;
        try{
            obj =joinPoint.proceed();
        }catch (Throwable e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return obj;
    }
}
