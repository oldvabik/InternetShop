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

    @Before(
            "execution(* com.oldvabik.internetshop..*(..))"
    )
    public void logBefore(JoinPoint joinPoint) {
        if (logger.isInfoEnabled()) {
            logger.info("CALL: {}", joinPoint.getSignature().toShortString());
        }
    }

    @AfterReturning(
            pointcut = "execution(* com.oldvabik.internetshop..*(..))", returning = "result"
    )
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        if (logger.isInfoEnabled()) {
            logger.info("EXECUTED: {} RETURNED: {}",
                    joinPoint.getSignature().toShortString(), result);
        }
    }

    @AfterThrowing(
            pointcut = "execution(* com.oldvabik.internetshop..*(..))", throwing = "error"
    )
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        if (logger.isErrorEnabled()) {
            logger.error("OCCURRED: {} REASON: {}",
                    joinPoint.getSignature().toShortString(), error.getMessage());
        }
    }

}
