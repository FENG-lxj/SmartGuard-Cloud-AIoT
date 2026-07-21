package com.chome.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description 设备操作码转换工具类
 * @Date 2025/7/22
 * @DAY_NAME_FULL: 星期二
 * @Version 1.0
 */

@Component
public class CodeTransferUtil {

    private static String prefixCode;

    /**
     * 解析数据库中的Base64加密后的操作码并进行拼接
     *
     * @param code 操作码
     * @param type 操作 00 开 | 01 关
     * @return 拼接后的操作码
     */
    public static byte[] transfer(String code, Double type) {
        // 拆分prefixCode
        String[] split = prefixCode.split(" ");

        int length = split.length + 2;
        byte[] bytes = new byte[length];

        // 计算中间位置
        int midIndex = split.length / 2;

        // 填充字节数组
        for (int i = 0, j = 0; i < bytes.length; i++) {
            if (i == midIndex) {
                // 插入code
                bytes[i] = (byte) Integer.parseInt(code, 16);
                // 插入type
                bytes[i] = (byte) (type.intValue() & 0xFF);
                i++;
            } else {
                // 复制原有值
                bytes[i] = (byte) Integer.parseInt(split[j], 16);
                j++;
            }
        }

        return bytes;
    }

    @Value("${tcp.operationCode}")
    public void setPrefixCode(String prefixCode) {
        CodeTransferUtil.prefixCode = prefixCode;
    }
}
