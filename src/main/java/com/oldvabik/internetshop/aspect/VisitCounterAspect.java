package com.oldvabik.internetshop.aspect;

import com.oldvabik.internetshop.service.VisitCounterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class VisitCounterAspect {

    private final VisitCounterService visitCounterService;

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void anyRestController() {}

    @AfterReturning("anyRestController()")
    public void countRequest() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            String uri = request.getRequestURI();

            if (!uri.equals("/api/visits/count")) {
                visitCounterService.increment(uri);
            }
        }
    }

}
