package com.kh.codereview.aireview.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "claude")
@Getter
@Setter
public class ClaudeProperties {

    private String apiKey;
    private String baseUrl;
    private String anthropicVersion;
    private String model;
    private int maxTokens;
    private int connectTimeoutMs;
    private int readTimeoutMs;
}
