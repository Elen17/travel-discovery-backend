package com.travel.discovery.config;

import com.travel.discovery.config.properties.LocalStorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Serves locally-stored uploads as static files at /uploads/**.
 * Only registered when running with the local storage provider.
 */
@Configuration
@ConditionalOnProperty(name = "storage.provider", havingValue = "local", matchIfMissing = true)
@RequiredArgsConstructor
public class LocalStorageConfig implements WebMvcConfigurer {

    private final LocalStorageProperties props;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path dir = Paths.get(props.getDirectory()).toAbsolutePath().normalize();
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations(dir.toUri().toString());
    }
}