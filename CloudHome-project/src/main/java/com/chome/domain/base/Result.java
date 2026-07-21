package com.chome.domain.base;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;

/**
 * @Description 统一返回结果类
 * @Date 2025/4/15
 * @DAY_NAME_FULL: 星期二
 * @Version 1.0
 */

@NoArgsConstructor
@Data
public class Result<T> implements Serializable {
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 提示信息
     */
    private String msg;
    /**
     * 数据
     */
    private T data;

    private Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    private static <T> Result<T> createSuccess(Integer code, String msg, T data) {
        return new Result<>(code, msg, data);
    }

    private static <T> Result<T> createError(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    public static <T> Result<T> success(T data) {
        return createSuccess(SC_OK, "success", data);
    }

    public static <T> Result<T> success() {
        return createSuccess(SC_OK, "success", null);
    }

    public static <T> Result<T> success(String msg) {
        return createSuccess(SC_OK, msg, null);
    }

    public static <T> Result<T> success(Integer code, String msg, T data) {
        return createSuccess(code, msg, data);
    }

    public static <T> Result<T> error(String msg) {
        return createError(SC_BAD_REQUEST, msg);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        return createError(code, msg);
    }


}

