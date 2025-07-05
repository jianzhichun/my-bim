package com.zjjqtech.bimplatform.infrastructure.limit;

import com.google.common.util.concurrent.RateLimiter;
import com.zjjqtech.bimplatform.infrastructure.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.zjjqtech.bimplatform.infrastructure.spring.SpringContextAware.$env;

/**
 * @author zao
 * @date 2020/09/23
 */
@Slf4j
@Aspect
@Component
public class RateLimiterAspect {

    private static final ConcurrentMap<String, com.google.common.util.concurrent.RateLimiter> RATE_LIMITER_CONCURRENT_MAP = new ConcurrentHashMap<>();

    @Pointcut("within(com.zjjqtech.bimplatform.controller..*) && (@within(org.springframework.stereotype.Controller) || @within(org.springframework.web.bind.annotation.RestController))")
    public void controller() { }

    @Around("controller()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        String key = point.getSignature().toString();
        Double limit = $env("rate-limit." + key, Double.class);
        if (null == limit) {
            limit = $env("rate-limit.all", Double.class);
        }
        if (null != limit) {
            limit(key, limit);
        }
        return point.proceed();
    }

    private void limit(String key, Double limit) {
        RateLimiter limiter = RATE_LIMITER_CONCURRENT_MAP.computeIfAbsent(key, k -> RateLimiter.create(limit));
        if (limiter.getRate() != limit) {
            limiter.setRate(limit);
        }
        if (!limiter.tryAcquire()) {
            throw new BizException("validate.error.access.limit");
        }
    }
}
