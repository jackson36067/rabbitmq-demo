package com.jackson;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SpringAMQPTest {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSendMessage() {
        // 队列名称
        String queueName = "simple.queue";
        // 交换机名称
        String exchange = "amq.fanout";
        // 信息

        String message = "hello rabbitMQ";
        rabbitTemplate.convertAndSend(exchange, queueName, message);
    }

    @Test
    public void testWorkSendMessage() throws InterruptedException {
        // 队列名称
        String queueName = "work";
        for (int i = 0; i < 50; i++) {
            String message = "hello rabbitMQ - " + i;
            rabbitTemplate.convertAndSend(queueName, message);
            Thread.sleep(20);
        }
    }
}
