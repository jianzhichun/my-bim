package com.zjjqtech.bimplatform.controller.utils;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * @author zao
 * @date 2020/06/15
 */
@Data
public class Result<T> implements Serializable {

    private boolean success;
    private List<String> errorMessages;
    private T content;

    public static <X> Result<X> of(X x) {
        Result<X> result = new Result<>();
        result.setSuccess(true);
        result.setContent(x);
        return result;
    }

    public static <X> Result<X> fail(String... errorMessages) {
        Result<X> result = new Result<>();
        result.setSuccess(false);
        result.setErrorMessages(Arrays.asList(errorMessages));
        return result;
    }

}
