package com.akulinski.userr8meservice.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.util.Arrays;

@Slf4j
@Aspect
public class LoggingAspect {

    @Before("execution(* com.akulinski.userr8meservice.web.*(..))")
    public void before(JoinPoint joinPoint) {
        log.info(String.format("Method %s Called with args %s", joinPoint.getSignature(), Arrays.toString(joinPoint.getArgs())));
    }
}
