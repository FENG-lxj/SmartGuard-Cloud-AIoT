package com.chome.constants;

/**
 * @Description InfluxDB Flux查询语句
 * @Date 2025/4/18
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 */

public class InfluxdbFlux {

    /**
     * 查询指定时间之后的数据
     */
    public static final String FLUX_QUERY_AFTER = """
            from(bucket: "CHome_Environment")
              |> range(start: %s)
              |> filter(fn: (r) => r._measurement == "environment")
              |> group(columns: ["_field"])
              |> last()""";

}
