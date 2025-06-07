package com.jackson.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置消费者消息接收失败的重发机制
 */
@Configuration
// 配置生效的条件, 需要开启消费者重试机制
 @ConditionalOnProperty(prefix = "spring.rabbitmq.listener.simple.retry", name = "enabled", havingValue = "true")
public class ErrorConfiguration {

    // 定义消息重发到的交换机以及队列
    @Bean
    public DirectExchange errorExchange() {
        return new DirectExchange("error.direct");
    }

    @Bean
    public Queue errorQueue() {
        return new Queue("error.queue");
    }

    @Bean
    public Binding errorBinding() {
        return BindingBuilder
                .bind(errorQueue())
                .to(errorExchange())
                .with("error");
    }

    // 定义消费者监听消息失败后对消息的处理机制 -> 这里使用消息重发到另一个交换机的机制
    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        // 重发消息到error.direct交换机 -> 可以监听该交换机,然后发送消息给开发者进行邮箱报警, 最后通过人工处理该条信息
        return new RepublishMessageRecoverer(rabbitTemplate, "error.direct", "error");
    }
}
