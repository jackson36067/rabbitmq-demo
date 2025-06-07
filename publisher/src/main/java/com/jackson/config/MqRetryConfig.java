package com.jackson.config;

import com.jackson.bean.MyCorrelationData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Configuration
@Slf4j
public class MqRetryConfig implements ApplicationContextAware {

    private static final int MAX_RETRY = 3;
    private static final long RETRY_DELAY_MS = 2000;
    private static final Map<String, MyCorrelationData> messageCache = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService retryExecutor = Executors.newScheduledThreadPool(4);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RabbitTemplate rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        rabbitTemplate.setMandatory(true);

        // ConfirmCallback - 消息是否到达 exchange
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("✅ 消息已到达交换机, correlationId={}", correlationData != null ? correlationData.getId() : "null");
                messageCache.remove(correlationData.getId());
            } else {
                log.warn("❌ 消息未到达交换机: {}, correlationId={}", cause, correlationData != null ? correlationData.getId() : "null");
                if (correlationData instanceof MyCorrelationData) {
                    retryLater((MyCorrelationData) correlationData, rabbitTemplate);
                }
            }
        });

        // ReturnsCallback - 消息到了交换机但无法路由到队列
        rabbitTemplate.setReturnsCallback(returned -> {
            String correlationId = returned.getMessage().getMessageProperties().getCorrelationId();
            log.warn("⚠️ 消息未路由到队列, correlationId={}, replyText={}", correlationId, returned.getReplyText());
            retryIfNeeded(correlationId, rabbitTemplate);
        });
    }

    private void retryLater(MyCorrelationData data, RabbitTemplate rabbitTemplate) {
        if (data.getRetryCount() < MAX_RETRY) {
            int nextRetry = data.getRetryCount() + 1;
            MyCorrelationData retryData = new MyCorrelationData(
                    UUID.randomUUID().toString(),
                    data.getExchange(),
                    data.getRoutingKey(),
                    data.getMessage(),
                    nextRetry
            );
            messageCache.put(retryData.getId(), retryData);
            retryExecutor.schedule(() -> {
                log.info("🔁 [ConfirmCallback] 第{}次重试, correlationId={}", nextRetry, retryData.getId());
                rabbitTemplate.convertAndSend(
                        retryData.getExchange(),
                        retryData.getRoutingKey(),
                        retryData.getMessage(),
                        retryData
                );
            }, RETRY_DELAY_MS, TimeUnit.MILLISECONDS);
        } else {
            log.error("❗ [ConfirmCallback] 达到最大重试次数({}), correlationId={}, message={}",
                    MAX_RETRY, data.getId(), data.getMessage());
        }
    }

    private void retryIfNeeded(String correlationId, RabbitTemplate rabbitTemplate) {
        MyCorrelationData data = messageCache.get(correlationId);
        if (data == null) {
            log.error("❌ [ReturnsCallback] 未找到 correlationId={} 的消息缓存", correlationId);
            return;
        }
        if (data.getRetryCount() < MAX_RETRY) {
            int nextRetry = data.getRetryCount() + 1;
            MyCorrelationData retryData = new MyCorrelationData(
                    UUID.randomUUID().toString(),
                    data.getExchange(),
                    data.getRoutingKey(),
                    data.getMessage(),
                    nextRetry
            );
            messageCache.put(retryData.getId(), retryData);
            retryExecutor.schedule(() -> {
                log.info("🔁 [ReturnsCallback] 第{}次重试, correlationId={}", nextRetry, retryData.getId());
                rabbitTemplate.convertAndSend(
                        retryData.getExchange(),
                        retryData.getRoutingKey(),
                        retryData.getMessage(),
                        retryData
                );
            }, RETRY_DELAY_MS, TimeUnit.MILLISECONDS);
        } else {
            log.error("❗ [ReturnsCallback] 达到最大重试次数({}), correlationId={}, message={}",
                    MAX_RETRY, correlationId, data.getMessage());
        }
    }
}
