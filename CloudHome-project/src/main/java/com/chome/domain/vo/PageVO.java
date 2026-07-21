package com.chome.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description 分页数据VO
 * @Date 2025/7/16
 * @DAY_NAME_FULL: 星期三
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageVO<T> {

    /**
     * 总数
     */
    private long total;
    /**
     * 分页数据
     */
    private List<T> data;
}
