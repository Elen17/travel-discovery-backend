package com.travel.discovery.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "storage.local")
@Data
public class LocalStorageProperties {

    /** Root folder on disk where uploads are written (relative to the working directory or absolute). */
    private String directory = "uploads";
    /** Base URL the files are served from; used to build the URLs returned to clients. */
    private String baseUrl = "http://localhost:8080";
    private String tempPrefix = "temp/";
    private String avatarPrefix = "avatars/";
}