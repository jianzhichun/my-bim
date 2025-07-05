package com.zjjqtech.bimplatform.infrastructure.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.zjjqtech.bimplatform.infrastructure.spring.SpringContextAware.$json;

/**
 * @author zao
 * @date 2020/09/23
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    @Pointcut("within(com.zjjqtech.bimplatform.controller..*) && (@within(org.springframework.stereotype.Controller) || @within(org.springframework.web.bind.annotation.RestController))")
    public void controller() { }

    @Pointcut("within(com.zjjqtech.bimplatform.service..*) && @within(org.springframework.stereotype.Service)")
    public void service() { }

    @Pointcut("within(com.zjjqtech.bimplatform.repository..*)")
    public void repository() {}

    @Around("controller() || service() || repository()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return point.proceed();
        } finally {
            MethodSignature signature = (MethodSignature)point.getSignature();
            log.info(String.join(
                "|",
                signature.getDeclaringTypeName() + "." + signature.getName(),
                $json().writeValueAsString(Arrays.stream(point.getArgs()).map(String::valueOf).collect(Collectors.toList())),
                String.valueOf(System.currentTimeMillis() - start)
            ));
        }
    }
}
