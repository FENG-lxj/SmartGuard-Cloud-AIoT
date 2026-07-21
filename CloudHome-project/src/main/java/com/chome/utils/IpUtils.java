package com.chome.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.lionsoul.ip2region.xdb.Version;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * @Description ip工具类
 * @Date 2025/9/15
 * @DAY_NAME_FULL: 星期一
 * @Version 1.0
 */

@Slf4j
@Component
public class IpUtils {

    /**
     * 获取客户端 IP
     *
     * @param request 请求对象
     * @return 客户端 IP
     */
    public static String getClientIp(HttpServletRequest request) {
        // 常见的代理头顺序
        String[] headerCandidates = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };

        for (String header : headerCandidates) {
            String ip = request.getHeader(header);
            if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
                continue;
            }
            // X-Forwarded-For 可能是 "client, proxy1, proxy2"，取首个
            if (ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }
            return ip;
        }

        String remoteAddr = request.getRemoteAddr();
        return remoteAddr == null ? "unknown" : remoteAddr;
    }

    /**
     * 获取 IP 所属城市信息
     *
     * @param ip 用户 IP
     * @return 城市信息
     */
    public static String getCityInfo(String ip) {
        try {
            // 1. 判断IP类型，选择对应的数据库文件和查询方式
            boolean isIpv6 = ip.contains(":");
            String dbPath;
            Version version;

            if (isIpv6) {
                // 获取 ip2region 的 IPV6 数据文件路径
                dbPath = Objects.requireNonNull(IpUtils.class.getResource("/data/ip2region_v6.xdb")).getPath();
                version = Version.IPv6;
            } else {
                // 获取 ip2region 的 IPV4 数据文件路径
                dbPath = Objects.requireNonNull(IpUtils.class.getResource("/data/ip2region_v4.xdb")).getPath();
                version = Version.IPv4;
            }

            // 2.1. 创建查询对象
            Searcher searcher = Searcher.newWithFileOnly(version, dbPath);
            // 2.2. 开始查询
            String address = searcher.search(ip);
            // 3.1. 处理地址信息
            String[] split = address.split("\\|");
            if (split.length < 4) {
                return "未知属地";
            }
            // 3.2. 判断查询结果的最小精度拼接(查询不到时为 0)
            StringBuilder minPrecision = new StringBuilder();
            for (int i = 0; i < 3; i++) {
                if (!"0".equals(split[i])) {
                    minPrecision.append(split[i]).append("·");
                }
            }
            minPrecision.delete(minPrecision.length() - 1, minPrecision.length());
            return minPrecision.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取 请求中 IP 所属城市信息
     * @param request 请求对象
     * @return 城市信息
     */
    public static String streamGetRegion(HttpServletRequest request) {
        String clientIp = getClientIp(request);
        try {
            return getCityInfo(clientIp);
        } catch (Exception e) {
            log.error("IP解析失败，clientIp={}, err={}", clientIp, e.getMessage());
            return "未知属地";
        }
    }
}
