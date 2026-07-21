package com.chome.utils;

import cn.hutool.core.lang.UUID;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

/**
 * @Description 阿里云OSS工具类
 * @Date 2025/5/20
 * @DAY_NAME_FULL: 星期二
 * @Version 1.0
 */

@RequiredArgsConstructor
@Component
@Slf4j
public class OssUtil {

    private final OSSClient ossClient;

    @Value("${thirdparty.aliyun.oss.backet}")
    private String bucketName;

    /**
     * 上传文件
     *
     * @param file 文件对象
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file) {
        try {
            // 1. 通过日期分割路径
            String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
            // 2. 拼接文件名
            String uuid = UUID.randomUUID().toString().replace("-", "");
            // 3. 文件后缀名
            String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String fileName = datePath + "/" +
                    new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_" + uuid +
                    "." + fileExtension;
            // 设置元数据：根据不同的后缀名动态匹配 Content-Type
            ObjectMetadata metadata = new ObjectMetadata();
            String contentType = Optional.ofNullable(fileExtension)
                    .map(ext -> switch (ext.toLowerCase()) {
                        case "jpg", "jpeg" -> "image/jpeg";
                        case "png" -> "image/png";
                        case "gif" -> "image/gif";
                        default -> "application/octet-stream"; // 默认二进制流
                    }).orElse("application/octet-stream");
            metadata.setContentType(contentType);
            // 2. 上传时使用新的路径
            ossClient.putObject(bucketName,
                    fileName,
                    file.getInputStream(),
                    metadata);
            // 3. 返回完整URL
            return "https://" + bucketName + "." + ossClient.getEndpoint() + "/" + fileName;
        } catch (IOException e) {
            log.error("文件上传失败：", e);
            throw new RuntimeException("文件上传失败", e);
        }
    }
}
