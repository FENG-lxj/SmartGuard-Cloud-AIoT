package com.chome.client;

import com.alibaba.fastjson2.JSONObject;
import com.chome.domain.entity.AlarmMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import static com.chome.constants.CHomeConstants.*;

/**
 * Spug平台消息推送客户端
 * 功能说明：
 * 1. 封装Spug第三方推送接口，提供两类能力：告警消息推送、短信验证码发送；
 * 2. 告警区分两种模式：电话+微信公众号推送、仅微信公众号推送；
 * 3. 统一封装POST请求公共方法，设置JSON请求头、解析接口返回状态码；
 * 4. 内置6位纯数字验证码生成工具，用于短信下发；
 * 5. 读取yml配置文件中Spug基础地址、各接口路径等常量，统一管理第三方地址
 */

@Component
@RequiredArgsConstructor
public class SpugPushClient {

    // HTTP远程请求工具，用于调用Spug平台POST接口
    private final RestTemplate restTemplate;

    /**
     * 配置文件读取：Spug服务基础根地址
     * 对应配置项 thirdparty.spug.base-url
     */
    @Value("${thirdparty.spug.base-url}")
    private String baseUrl;

    /**
     * 配置文件读取：电话+微信双渠道告警接口后缀路径
     * 对应配置项 thirdparty.spug.all-alarm
     */
    @Value("${thirdparty.spug.all-alarm}")
    private String allAlarm;

    /**
     * 配置文件读取：仅微信公众号告警接口后缀路径
     * 对应配置项 thirdparty.spug.wxmp-alarm
     */
    @Value("${thirdparty.spug.wxmp-alarm}")
    private String wxmpAlarm;

    /**
     * 配置文件读取：短信验证码下发接口后缀路径
     * 对应配置项 thirdparty.spug.sms-code
     */
    @Value("${thirdparty.spug.sms-code}")
    private String smsCode;

    /**
     * 推送告警消息入口方法
     * @param message 告警实体，存储告警文本、类型、等级、告警时间等业务信息
     * @param mode 推送模式：ALL_ALARM_MODE(电话+公众号) / WX_ALARM_MODE(仅公众号)
     * @param phoneNum 接收告警的目标手机号
     */
    public void pushAlarm(AlarmMessage message, String mode, String phoneNum) {
        // 拼接完整请求地址
        String pushUrl = baseUrl;
        Map<String, Object> request = new HashMap<>();

        // 判断推送模式，填充差异化参数
        if (ALL_ALARM_MODE.equals(mode)) {
            // 电话+微信渠道：需要传入告警正文、接收手机号
            pushUrl += allAlarm;
            request.put("message", message.getAlarmText());
            request.put("targets", phoneNum);
        } else if (WX_ALARM_MODE.equals(mode)) {
            // 仅微信渠道：无需单独传入手机号，接口内部按订阅推送
            pushUrl += wxmpAlarm;
        }

        // 统一填充公共告警字段
        String alarmTime = message.getAlarmTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        request.put("title", ALARM_TITLE);
        request.put("content", message.getAlarmType());
        request.put("level", message.getAlarmLevel().getDescription());
        request.put("time", alarmTime);
        request.put("reason", message.getAlarmText());

        // 调用公共POST请求方法推送告警
        processPost(pushUrl, request);
    }

    /**
     * 向指定手机号发送短信验证码
     * @param phoneNum 接收验证码的手机号
     * @return 生成的6位数字验证码；接口调用失败返回null
     */
    public String pushSmsCode(String phoneNum) {
        // 拼接短信验证码接口完整地址
        String pushUrl = baseUrl + smsCode;
        Map<String, Object> request = new HashMap<>();

        // 生成6位数字验证码
        String code = generateSixDigitCode();

        // 填充短信接口请求参数
        request.put("targets", phoneNum);
        request.put("projectName", PROJECT_NAME);
        request.put("code", code);
        request.put("overTime", CODE_EXPIRATION_TIME);

        // 接口调用成功返回验证码，失败返回null
        return processPost(pushUrl, request) ? code : null;
    }

    /**
     * 生成6位纯数字验证码工具方法
     * @return 000000 ~ 999999 区间6位数字字符串
     */
    private String generateSixDigitCode() {
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        // 循环6次，每次随机取0-9单个数字拼接
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * POST请求公共处理方法
     * 统一封装JSON请求头、发起POST调用、解析响应判断接口是否成功
     * @param url 完整请求地址
     * @param request 接口请求参数Map
     * @return true=接口返回code=200调用成功；false=请求异常/状态码非200
     */
    private boolean processPost(String url, Map<String, Object> request) {
        // 构造请求头，指定请求体为JSON格式
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        // 发起POST请求，接收原始字符串响应
        String response = restTemplate.postForObject(url, entity, String.class);

        // 响应为空直接判定调用失败
        if (response == null) {
            return false;
        }
        try {
            // 使用fastjson2解析JSON，读取返回码code，等于200代表业务成功
            JSONObject jsonResponse = JSONObject.parseObject(response);
            return Integer.valueOf(200).equals(jsonResponse.getInteger("code"));
        } catch (Exception e) {
            // JSON解析失败兜底方案：直接字符串匹配判断是否包含成功标识
            return response.contains("\"code\": 200");
        }
    }
}