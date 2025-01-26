package com.jackson.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FanoutConfiguration {

    // 声明fanout交换机
    @Bean
    public FanoutExchange genFanoutExchange() {
        return ExchangeBuilder.fanoutExchange("jackson.fanout2").build();
    }

    // 声明队列
    @Bean
    public Queue genFanoutQueue3() {
        return QueueBuilder.durable("fanout.queue3").build();
    }

    @Bean
    public Queue genFanoutQueue4() {
        return QueueBuilder.durable("fanout.queue4").build();
    }

    // 声明binding -> 交换机与队列的联系
    @Bean
    public Binding genBindingFanout2() {
        return BindingBuilder.bind(genFanoutQueue3()).to(genFanoutExchange());
    }
    @Bean
    public Binding genBindingFanout3() {
        return BindingBuilder.bind(genFanoutQueue4()).to(genFanoutExchange());
    }
}
