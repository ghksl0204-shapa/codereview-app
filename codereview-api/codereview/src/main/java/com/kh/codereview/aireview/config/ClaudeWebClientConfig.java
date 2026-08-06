package com.kh.codereview.aireview.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ClaudeWebClientConfig {

    private final ClaudeProperties claudeProperties;

    @Bean
    public WebClient claudeWebClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, claudeProperties.getConnectTimeoutMs())
                .responseTimeout(Duration.ofMillis(claudeProperties.getReadTimeoutMs()));

        return WebClient.builder()
                .baseUrl(claudeProperties.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader("x-api-key", claudeProperties.getApiKey())
                .defaultHeader("anthropic-version", claudeProperties.getAnthropicVersion())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
    }
}
