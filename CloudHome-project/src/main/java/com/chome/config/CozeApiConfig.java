package com.chome.config;

import com.coze.openapi.service.auth.TokenAuth;
import com.coze.openapi.service.config.Consts;
import com.coze.openapi.service.service.CozeAPI;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;



@Configuration
public class CozeApiConfig {

    @Value("${thirdparty.coze.token}")
    private String token;

    @Bean
    public CozeAPI cozeApi() {
        // 创建 CozeAPI 实例
        // 创建TokenAuth对象，传入PAT作为身份认证凭据
        TokenAuth authCli = new TokenAuth(token);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        // 构建CozeAPI客户端实例，配置连接参数
        return new CozeAPI.Builder()
                .baseURL(Consts.COZE_CN_BASE_URL)
                .auth(authCli)
                .client(httpClient)
                .build();
    }
}
