package com.chome.domain.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @Description 分页参数
 * @Date 2025/4/15
 * @DAY_NAME_FULL: 星期二
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequest {
    /**
     * 当前页码，从1开始
     */
    @Min(value = 1, message = "当前页码必须大于0")
    private int pageNum;
    /**
     * 每页大小
     */
    @Min(value = 1, message = "当前页码必须大于0")
    private int pageSize;
    /**
     * 总记录数
     */
    private long total;
    /**
     * 条件查询字段
     */
    private Map<String, Object> queryConditions;
}