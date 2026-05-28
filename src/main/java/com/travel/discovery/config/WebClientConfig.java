package com.travel.discovery.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${csc.api.base-url}")
    private String cscBaseUrl;

    @Value("${csc.api.key}")
    private String cscApiKey;

    @Bean
    public WebClient cscWebClient() {
        return WebClient.builder()
            .baseUrl(cscBaseUrl)
            .defaultHeader("X-CSCAPI-KEY", cscApiKey)
            .build();
    }
}
