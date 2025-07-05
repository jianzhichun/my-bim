package com.zjjqtech.bimplatform.controller.utils;

import com.zjjqtech.bimplatform.infrastructure.exception.BizException;
import com.zjjqtech.bimplatform.infrastructure.spring.SpringContextAware;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import static com.zjjqtech.bimplatform.infrastructure.spring.SpringContextAware.$t;

/**
 * @author zao
 */
@Slf4j
@ControllerAdvice(basePackages = "com.zjjqtech.bimplatform.controller")
public class BimControllerAdvice implements ResponseBodyAdvice<Object> {

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public Result<?> exception(final Throwable throwable) {
        if (log.isDebugEnabled()) {
            log.debug("Error, ", throwable);
        }
        if (throwable instanceof BizException) {
            return Result.fail($t(((BizException) throwable).getCode()));
        } else if (throwable instanceof AccessDeniedException) {
            return Result.fail($t("validate.error.access.denied"));
        } else if (throwable instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException e = (MethodArgumentNotValidException) throwable;
            return Result.fail(e.getBindingResult().getAllErrors().stream().map(ObjectError::getDefaultMessage).map(SpringContextAware::$t).toArray(String[]::new));
        } else if (throwable instanceof ConstraintViolationException) {
            ConstraintViolationException e = (ConstraintViolationException) throwable;
            return Result.fail(e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).map(SpringContextAware::$t).toArray(String[]::new));
        } else {
            String errorMessage = (throwable != null ? throwable.getMessage() : "validate.error.unknown");
            return Result.fail($t(errorMessage));
        }
    }

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return !ResponseEntity.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        return body instanceof Result ? body : Result.of(body);
    }
}