package com.jackson.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SpringRabbitListener {

    @RabbitListener(queues = "simple.queue")
    private void listenerSimpleQueueMessage(String message) {
        log.info("监听到队列simple.queue中发送的消息: {}", message);
        // 执行中间逻辑
        log.info("消息处理完毕");
    }

    /**
     * rabbitMQ-work模型的使用
     * 当一个队列有多个消费者消费时,可以加快处理消息的速度
     * 但是这时消息只会轮询给每个消费者,但是work模型需要实现能者多劳的情景
     * 加上perfetch配置,让消费者消费完一条消息才能继续消费消息,这样就可以实现能者多劳
     * @param message
     */
    @RabbitListener(queues = "work")
    private void listenerWorkMessage1(String message) throws InterruptedException {
        log.info("监听到消费者1 中发送的消息: {}", message);
        Thread.sleep(20);
    }
    @RabbitListener(queues = "work")
    private void listenerWorkMessage2(String message) throws InterruptedException {
        log.error("监听到消费者2 中发送的消息: {}", message);
        Thread.sleep(200);
    }
}
