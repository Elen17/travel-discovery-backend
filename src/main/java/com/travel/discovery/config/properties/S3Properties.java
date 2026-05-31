package com.travel.discovery.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aws.s3")
@Data
public class S3Properties {

    private String region = "us-east-1";
    private String bucket;
    private String accessKey;
    private String secretKey;
    private String tempPrefix = "temp/";
    private String avatarPrefix = "avatars/";
    /** Optional CDN / custom domain. If blank, the public URL is derived from bucket + region. */
    private String publicBaseUrl;
}