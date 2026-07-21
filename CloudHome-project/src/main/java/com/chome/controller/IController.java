package com.chome.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description 统一接口
 * @Date 2025/8/6
 * @DAY_NAME_FULL: 星期三
 * @Version 1.0
 */

public interface IController {

    ValidatorFactory FACTORY = Validation.buildDefaultValidatorFactory();
    Validator VALIDATOR = FACTORY.getValidator();

    /**
     * 手动校验多个对象，若存在错误则抛出 IllegalArgumentException
     *
     * @param objects 需要校验的对象
     */
    default void validate(Object... objects) {
        for (Object obj : objects) {
            Set<ConstraintViolation<Object>> violations = IController.VALIDATOR.validate(obj);
            if (!violations.isEmpty()) {
                String errors = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining("；\n"));
                throw new IllegalArgumentException("参数校验失败：" + errors);
            }
        }
    }
}
