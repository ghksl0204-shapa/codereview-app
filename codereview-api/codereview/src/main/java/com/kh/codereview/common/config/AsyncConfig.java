package com.kh.codereview.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    // AI 리뷰 생성(외부 API 호출)을 요청 스레드와 분리해서 처리하기 위한 전용 스레드 풀.
    // t3.small 규모라 넉넉하게 잡지 않고 소수 스레드 + 대기 큐로 구성.
    @Bean(name = "aiReviewExecutor")
    public Executor aiReviewExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("ai-review-");
        executor.initialize();
        return executor;
    }
}
