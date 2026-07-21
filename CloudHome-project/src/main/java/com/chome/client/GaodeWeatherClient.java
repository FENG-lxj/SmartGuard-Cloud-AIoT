package com.chome.client;

import com.chome.domain.dto.QueryWeatherDTO;
import com.chome.domain.vo.WeatherResponse;
import com.chome.mapper.CityMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 高德开放平台天气查询客户端
 * 功能说明：
 * 1. 封装高德天气API的HTTP请求逻辑，提供统一查询天气方法；
 * 2. 根据传入城市名称，通过CityMapper查询对应城市adcode编码；
 * 3. 拼接高德标准请求地址，携带密钥、返回格式、城市编码、天气扩展类型参数；
 * 4. 使用RestTemplate发起GET请求获取JSON字符串；
 * 5. Jackson反序列化JSON为WeatherResponse实体，自动忽略返回字段中未定义的属性；
 * 6. 校验高德接口status状态码，非1代表接口调用异常，主动抛出业务异常；
 * 7. 捕获序列化、网络等全部异常，统一包装为运行时异常向上抛出，交由全局异常处理器处理
 */
@Component
@RequiredArgsConstructor
public class GaodeWeatherClient {

    /**
     * 读取配置文件：高德接口返回数据格式(json/xml)
     * 对应配置项 thirdparty.gaode.output
     */
    @Value("${thirdparty.gaode.output}")
    private String output;

    /**
     * 读取配置文件：高德开放平台开发者密钥key
     * 对应配置项 thirdparty.gaode.key
     */
    @Value("${thirdparty.gaode.key}")
    private String key;

    // HTTP请求工具，用于发起第三方API GET调用
    private final RestTemplate restTemplate;

    // 城市数据库Mapper，根据城市名称查询高德标准城市编码adcode
    private final CityMapper cityMapper;

    /**
     * Jackson序列化/反序列化工具实例
     * 配置：DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES = false
     * 作用：反序列化JSON时，如果返回字段在实体类不存在，不抛出解析异常，直接忽略未知字段
     */
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     * 查询高德天气信息统一入口方法
     * @param queryWeatherDTO 天气查询入参DTO，包含城市名称、天气扩展类型(base/all)
     * @return WeatherResponse 高德天气接口标准响应实体，封装实况/预报天气数据
     */
    public WeatherResponse queryWeather(QueryWeatherDTO queryWeatherDTO) {
        // 1. 根据传入城市名称查询数据库中存储的高德城市编码adcode
        String adCode = cityMapper.getCityCodeByName(queryWeatherDTO.getCity());

        // 2. 使用UriComponentsBuilder安全拼接GET请求URL，自动处理参数编码，避免特殊字符报错
        String url = UriComponentsBuilder.fromHttpUrl("https://restapi.amap.com/v3/weather/weatherInfo")
                .queryParam("key", key)                // 高德开发者密钥
                .queryParam("output", output)          // 数据返回格式json
                .queryParam("city", adCode)            // 城市adcode编码
                .queryParam("extensions", queryWeatherDTO.getExtensions()) // 天气类型：base实况 / all预报+实况
                .toUriString();

        // 3. RestTemplate发起GET请求，接收接口返回原始JSON字符串
        String weatherJson = restTemplate.getForObject(url, String.class);
        try {
            // 4. Jackson将JSON字符串反序列化为WeatherResponse响应实体
            // TypeReference<> 用于处理泛型类型，保证泛型VO完整解析
            WeatherResponse response = objectMapper.readValue(weatherJson, new TypeReference<>() {});

            // 5. 校验高德接口状态码：status=1 代表请求成功，其他值为调用失败
            if (!"1".equals(response.getStatus())) {
                throw new RuntimeException("高德 API 调用失败: " + response.getInfo());
            }
            // 接口调用正常，返回封装后的天气实体
            return response;
        } catch (Exception e) {
            // 捕获JSON解析异常、网络异常等所有异常，统一包装后抛出
            throw new RuntimeException("解析高德天气响应失败", e);
        }
    }
}