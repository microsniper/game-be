package com.sniper.game.wordgame.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云 OSS 配置（application.yml 的 oss.*）
 */
@Data
@Component
@ConfigurationProperties(prefix = "oss")
public class OssProperties {

    /** 地域节点，如 oss-cn-beijing.aliyuncs.com */
    private String endpoint;

    /** Bucket 名称 */
    private String bucket;

    private String accessKeyId;

    private String accessKeySecret;

    /** 对外访问域名（CDN/自定义域名），拼接对象 key 即为完整 URL */
    private String publicDomain;
}
