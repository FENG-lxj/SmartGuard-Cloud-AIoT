package com.chome.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description 字符串转换工具类
 * @Date 2025/7/21
 * @DAY_NAME_FULL: 星期一
 * @Version 1.0
 */

public class TranscodingUtil {

    /**
     * 将 Unicode 编码字符串转换为 UTF-8 字符串
     * @param input Unicode 编码字符串，如 "\\u53d1\\u73b0\\u8dcc\\u501f"
     * @return 转换后的中文字符串
     */
    public static String unicodeToUtf8(String input) {
        StringBuilder sb = new StringBuilder();
        Matcher matcher = Pattern.compile("\\\\u([0-9a-fA-F]{4})").matcher(input);
        while (matcher.find()) {
            int codePoint = Integer.parseInt(matcher.group(1), 16);
            matcher.appendReplacement(sb, new String(Character.toChars(codePoint)));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 将字符串转换为 MySQL 的驼峰命名法
     * @param camelCaseName 驼峰命名法字符串
     * @return 转换后的字符串
     */
    public static String MysqlCamelCasing(String camelCaseName) {
        StringBuilder result = new StringBuilder();
        if (!camelCaseName.isEmpty()) {
            result.append(Character.toLowerCase(camelCaseName.charAt(0)));
            for (int i = 1; i < camelCaseName.length(); i++) {
                char ch = camelCaseName.charAt(i);
                if (Character.isUpperCase(ch)) {
                    result.append("_");
                    result.append(Character.toLowerCase(ch));
                } else {
                    result.append(ch);
                }
            }
        }
        return result.toString();
    }
}
