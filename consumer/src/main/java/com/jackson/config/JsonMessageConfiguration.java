package com.jackson.config;

import com.rabbitmq.client.MessageProperties;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonMessageConfiguration {

    /**
     * 使用json消息转换器
     *
     * @return
     */
    @Bean
    public MessageConverter JsonMessageConverter() {
        Jackson2JsonMessageConverter jsonMessageConverter = new Jackson2JsonMessageConverter();
        // 给每个消息设置一个唯一id, 防止消息重复接收, 可以将该消息id保存到Redis中判断是否发送过消息
        jsonMessageConverter.setCreateMessageIds(true);
        return jsonMessageConverter;
    }
}
