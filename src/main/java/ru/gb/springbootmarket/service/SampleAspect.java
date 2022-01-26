package ru.gb.springbootmarket.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class SampleAspect {

    private static Long psWorkTime = 0L;
    private static Long usWorkTime = 0L;
    private static Long osWorkTime = 0L;

    public static Long getPsWorkTime() {
        return psWorkTime;
    }

    public static Long getUsWorkTime() {
        return usWorkTime;
    }

    public static Long getOsWorkTime() {
        return osWorkTime;
    }

    @Around("within(ProductService)")
    public Object methodProfilingProductServise(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long begin = System.currentTimeMillis();
        Object obj = proceedingJoinPoint.proceed();
        long end = System.currentTimeMillis();
        psWorkTime = psWorkTime + (end - begin);
        return obj;
    }

    @Around("within(UserService)")
    public Object methodProfilingUserService(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long begin = System.currentTimeMillis();
        Object obj = proceedingJoinPoint.proceed();
        long end = System.currentTimeMillis();
        usWorkTime = usWorkTime + (end - begin);
        return obj;
    }

    @Around("within(OrderService)")
    public Object methodProfilingOrderService(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long begin = System.currentTimeMillis();
        Object obj = proceedingJoinPoint.proceed();
        long end = System.currentTimeMillis();
        osWorkTime = osWorkTime + (end - begin);
        return obj;
    }

}
