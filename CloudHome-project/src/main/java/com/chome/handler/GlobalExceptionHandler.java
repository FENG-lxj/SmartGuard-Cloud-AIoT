package com.chome.handler;

import com.chome.domain.base.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Description 全局统一异常处理器
 * 拦截所有@RestController接口抛出的异常，统一打印日志、封装标准化返回体Result
 * 分层捕获参数异常、业务运行时异常、全局未知系统异常，区分日志级别与提示文案
 * @Date 2025/7/25
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 */
// 全局Rest接口增强，拦截所有控制器抛出的异常
@RestControllerAdvice
// 自动生成log日志对象
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 兜底捕获所有未单独处理的通用异常（系统底层未知异常）
     * @param e 异常对象
     * @return 统一错误返回体，隐藏具体错误信息，仅提示联系管理员
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        // 系统级异常使用error级别日志，完整打印堆栈用于排查
        log.error("系统异常", e);
        return Result.error("系统异常，请联系管理员");
    }

    /**
     * 捕获业务自定义运行时异常
     * @param e 业务运行时异常
     * @return 返回异常内自定义的业务提示信息
     */
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e) {
        log.error("业务异常", e);
        return Result.error(e.getMessage());
    }

    /**
     * 捕获参数非法异常：参数为空、格式错误、参数不合法等
     * @param e 参数校验异常
     * @return 返回具体参数错误提示
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result handleIllegalArgumentException(IllegalArgumentException e) {
        // 参数错误属于可预期用户操作问题，使用info日志级别
        log.info("参数异常", e);
        return Result.error(e.getMessage());
    }

}