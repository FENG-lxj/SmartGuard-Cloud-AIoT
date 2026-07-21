package com.chome.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.HttpUrl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @Description 阿里云OSS配置类
 * @Date 2025/6/15
 * @DAY_NAME_FULL: 星期日
 * @Version 1.0
 */

@Component
public class SparkRequestUtil {

    private static final Gson gson = new Gson();

    @Value("${thirdparty.spark.ai.appid}")
    private String appid;

    @Value("${thirdparty.spark.ai.Speaker}")
    private String Speaker;

    @Value("${thirdparty.spark.ai.appkey}")
    private String appkey;

    @Value("${thirdparty.spark.ai.appsecret}")
    private String appsecret;

    /**
     * 构建ASR音频数据
     *
     * @param audioData 音频数据
     * @param status    状态
     * @param seq       序号
     * @return 音频数据
     */
    private static JsonObject buildAudioPayload(byte[] audioData, int status, int seq) {
        String audioBase64 = Base64.getEncoder().encodeToString(audioData);

        JsonObject audio = new JsonObject();
        audio.addProperty("encoding", "raw");
        audio.addProperty("sample_rate", 16000);
        audio.addProperty("channels", 1);
        audio.addProperty("bit_depth", 16);
        audio.addProperty("seq", seq);
        audio.addProperty("status", status);
        audio.addProperty("audio", audioBase64);

        JsonObject payload = new JsonObject();
        payload.add("audio", audio);

        return payload;
    }

    /**
     * 获取鉴权url
     *
     * @param hostUrl 请求地址
     * @return 鉴权url
     * @throws Exception 获取鉴权url异常
     */
    public String getAuthUrl(String hostUrl) throws Exception {
        /**
         * 参数				 类型       必须   说明                                            示例
         * host			    string	    是    请求主机                                         iat.cn-huabei-1.xf-yun.com
         * date			    string	    是    当前时间戳，RFC1123格式                           Wed,10 Jul 2019 07:35:43 GMT
         * authorization	string	    是	  使用base64编码的签名相关信息(签名基于hmac-sha256)	 请去官方文档查看）
         */
        URL url = new URL(hostUrl);
        // data
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        // authorization 中使用到的 signature（未签名）
        String builder = "host: " + url.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + url.getPath() + " HTTP/1.1";

        // 对 signature 进行签名后base64编码
        Charset charset = StandardCharsets.UTF_8;
        Mac mac = Mac.getInstance("hmacsha256");
        // 使用hmac-sha256算法结合appsecret对signature_origin签名，获得签名后的摘要signature_sha
        SecretKeySpec spec = new SecretKeySpec(appsecret.getBytes(charset), "hmacsha256");
        mac.init(spec);
        byte[] hexDigits = mac.doFinal(builder.getBytes(charset));
        // 使用base64编码对signature_sha进行编码获得最终的signature
        String sha = Base64.getEncoder().encodeToString(hexDigits);

        // authorization_origin 编码前拼接
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", appkey, "hmac-sha256", "host date request-line", sha);
        // 拼接 https 请求和鉴权参数 authorization date host
        HttpUrl httpUrl = HttpUrl.parse("https://" + url.getHost() + url.getPath()).newBuilder().//
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(charset))).//
                addQueryParameter("date", date).//
                addQueryParameter("host", url.getHost()).//
                build();
        // 将url中的 schema http://和https://分别替换为ws:// 和 wss:// 后返回
        return httpUrl.toString().replace("http://", "ws://").replace("https://", "wss://");
    }

    /**
     * 构建ASR首帧请求
     *
     * @param audioData 音频数据
     * @return 请求数据
     */
    public String buildASRFirstFrame(byte[] audioData) {
        JsonObject header = new JsonObject();
        header.addProperty("app_id", appid);
        header.addProperty("status", 0);

        JsonObject iatParams = new JsonObject();
        iatParams.addProperty("domain", "slm");
        iatParams.addProperty("language", "mul_cn");
        iatParams.addProperty("accent", "mandarin");
        iatParams.addProperty("eos", 6000);
        iatParams.addProperty("vinfo", 1);

        JsonObject resultParams = new JsonObject();
        resultParams.addProperty("encoding", "utf8");
        resultParams.addProperty("compress", "raw");
        resultParams.addProperty("format", "json");
        iatParams.add("result", resultParams);

        JsonObject parameter = new JsonObject();
        parameter.add("iat", iatParams);

        JsonObject audioPayload = buildAudioPayload(audioData, 0, 0);

        JsonObject request = new JsonObject();
        request.add("header", header);
        request.add("parameter", parameter);
        request.add("payload", audioPayload);

        return gson.toJson(request);
    }

    /**
     * 构建ASR中间帧/末帧
     *
     * @param audioData 音频数据
     * @param status    0-中间帧 1-末帧
     * @param seq       帧序号
     * @return 帧数据
     */
    public String buildASRFrame(byte[] audioData, int status, int seq) {
        JsonObject header = new JsonObject();
        header.addProperty("app_id", appid);
        header.addProperty("status", status);

        JsonObject audioPayload = buildAudioPayload(audioData, status, seq);

        JsonObject request = new JsonObject();
        request.add("header", header);
        request.add("payload", audioPayload);

        return gson.toJson(request);
    }

    /**
     * 构建TTS请求
     *
     * @param nlpText 文本对话结果
     * @return 请求报文
     */
    public String buildTtsRequest(String nlpText, int status, int seq) {
        //发送数据,求数据均为json字符串
        JsonObject sendData = new JsonObject();
        JsonObject parameter = new JsonObject();
        JsonObject payload = new JsonObject();
        if (status == 0) {
            //填充header开始********************************************************
            JsonObject header = new JsonObject();
            header.addProperty("app_id", appid);
            header.addProperty("status", status);
            //填充header结束********************************************************
            //填充parameter开始********************************************************
            JsonObject oral = new JsonObject();
            oral.addProperty("spark_assist", 1);
            oral.addProperty("oral_level", "mid");

            JsonObject tts = new JsonObject();
            tts.addProperty("vcn", Speaker); // 发音人
            tts.addProperty("speed", 50);
            tts.addProperty("volume", 50);
            tts.addProperty("pitch", 50);
            tts.addProperty("bgs", 0);
            tts.addProperty("reg", 0);
            tts.addProperty("rdn", 0);
            tts.addProperty("rhy", 0);
            // audio 输出格式
            JsonObject audio = new JsonObject();
            audio.addProperty("encoding", "lame");// mp3格式
            audio.addProperty("sample_rate", 24000);
            audio.addProperty("channels", 1);
            audio.addProperty("bit_depth", 16);
            audio.addProperty("frame_size", 0);

            tts.add("audio", audio);
            parameter.add("tts", tts);
            sendData.add("header", header);
        }

        //填充parameter结束********************************************************
        //填充payload开始********************************************************
        JsonObject text = new JsonObject();
        text.addProperty("encoding", "utf8");
        text.addProperty("compress", "raw");
        text.addProperty("format", "json");
        text.addProperty("status", status);
        text.addProperty("seq", seq);
        text.addProperty("text", Base64.getEncoder().encodeToString(nlpText.getBytes(StandardCharsets.UTF_8)));
        payload.add("text", text);
        sendData.add("parameter", parameter);
        sendData.add("payload", payload);
        return sendData.toString();
    }

}