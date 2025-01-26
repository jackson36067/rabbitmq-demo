package com.jackson.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// @Configuration
public class DirectConfiguration {

    // 声明direct交换机
    @Bean
    public DirectExchange genDirectExchange() {
        return ExchangeBuilder.directExchange("jackson.direct2").build();
    }

    // 声明队列
    @Bean
    public Queue genDirectQueue3() {
        return QueueBuilder.durable("direct.queue3").build();
    }
    @Bean
    public Queue genDirectQueue4() {
        return QueueBuilder.durable("direct.queue4").build();
    }

    // 声明binding -> 交换机与队列的联系
    @Bean
    public Binding genBindingRedQueue3() {
        return BindingBuilder.bind(genDirectQueue3()).to(genDirectExchange()).with("red");
    }
    @Bean
    public Binding genBindingRedQueue4() {
        return BindingBuilder.bind(genDirectQueue4()).to(genDirectExchange()).with("red");
    }
    @Bean
    public Binding genBindingBlueQueue3() {
        return BindingBuilder.bind(genDirectQueue3()).to(genDirectExchange()).with("blue");
    }
    @Bean
    public Binding genBindingYellowQueue4() {
        return BindingBuilder.bind(genDirectQueue4()).to(genDirectExchange()).with("yellow");
    }
}
