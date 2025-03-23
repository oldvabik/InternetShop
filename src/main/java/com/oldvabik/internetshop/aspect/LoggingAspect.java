package com.oldvabik.internetshop.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.oldvabik.internetshop..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        if (logger.isDebugEnabled()) {
            logger.info("Executing: {}", joinPoint.getSignature().toShortString());
        }
    }

    @AfterReturning(pointcut = "execution(* com.oldvabik.internetshop..*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        if (logger.isDebugEnabled()) {
            logger.info("Executing: {} with result: {}", joinPoint.getSignature().toShortString(), result);
        }
    }

    @AfterThrowing(pointcut = "execution(* com.oldvabik.internetshop..*(..))", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        if (logger.isDebugEnabled()) {
            logger.error("Exception in: {} with cause: {}",
                    joinPoint.getSignature().toShortString(), error.getMessage());
        }
    }

}
