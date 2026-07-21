package com.chome.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @Description TCP底层Socket连接工厂配置类
 * 统一封装TCP客户端Socket创建逻辑，读取配置文件中的连接地址、端口、超时时间
 * 提供两种创建Socket方法：抛异常版、静默失败返回null版，方便业务按需调用
 * @Date 2025/7/18
 * @DAY_NAME_FULL: 星期五
 * @Version 1.0
 */
@Configuration
public class TCPConnectionFactory {

    // TCP服务端IP地址，读取配置文件tcp.host
    @Value("${tcp.host}")
    private String host;

    // TCP服务端端口号，读取配置文件tcp.port
    @Value("${tcp.port}")
    private Integer port;

    // TCP建立连接超时时间（单位毫秒），读取配置文件tcp.connectTimeOut
    @Value("${tcp.connectTimeOut}")
    private Integer connectTimeOut;

    /**
     * 创建TCP Socket连接，连接失败会抛出IO异常，由上层业务捕获处理
     * 连接失败时自动关闭Socket资源，避免文件句柄泄漏
     * @return 已成功建立连接的Socket对象
     * @throws IOException 网络连接失败、超时、服务不可达等IO异常
     */
    public Socket createSocket() throws IOException {
        // 初始化空Socket对象
        Socket socket = new Socket();
        try {
            // 发起TCP连接，设置连接超时
            socket.connect(new InetSocketAddress(host, port), connectTimeOut);
            return socket;
        } catch (IOException e) {
            // 连接异常，主动关闭Socket释放资源
            try {
                socket.close();
            } catch (IOException closeException) {
                // 关闭时出现的异常无需向上抛出，直接忽略
            }
            // 抛出原始连接异常，交给调用方处理
            throw e;
        }
    }

    /**
     * 静默创建TCP Socket，内部捕获所有IO异常，失败直接返回null，不向外抛异常
     * 适合不需要精细处理连接失败、仅判断是否为空即可的业务场景
     * @return 连接成功返回Socket；连接失败返回null
     */
    public Socket createSocketSilently() {
        try {
            // 调用标准创建方法
            return createSocket();
        } catch (IOException e) {
            // 捕获异常，返回null标识连接失败
            return null;
        }
    }
}