package com.zjjqtech.bimplatform.infrastructure.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zao
 * @date 2020/09/21
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BizException extends RuntimeException {

    private String code;

    public BizException(String code) {
        super(code);
        this.code = code;
    }

    public BizException(String message, String code) {
        super(message);
        this.code = code;
    }

    public BizException(String message, Throwable cause, String code) {
        super(message, cause);
        this.code = code;
    }

    public BizException(Throwable cause, String code) {
        super(cause);
        this.code = code;
    }

    public BizException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String code) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }
}
