package com.jackson.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 异步重试线程池
 */
@Configuration
public class RetryExecutorConfig {
    @Bean("retryExecutor")
    public ScheduledExecutorService retryExecutor() {
        return Executors.newScheduledThreadPool(2);
    }
}
