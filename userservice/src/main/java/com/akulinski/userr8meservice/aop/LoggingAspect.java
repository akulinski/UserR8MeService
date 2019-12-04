package com.akulinski.userr8meservice.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
@Profile("log-web")
public class LoggingAspect {

    @Before("within(com.akulinski.userr8meservice.web.rest.*)")
    public void before(JoinPoint joinPoint) {
        log.info(String.format("Method %s Called with args %s", joinPoint.getSignature(), Arrays.toString(joinPoint.getArgs())));
    }
}
