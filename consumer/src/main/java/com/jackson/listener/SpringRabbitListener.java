package com.jackson.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class SpringRabbitListener {

    @RabbitListener(queues = "simple.queue")
    private void listenerSimpleQueueMessage(String message) {
        log.info("监听到队列simple.queue中发送的消息: {}", message);
        // 执行中间逻辑
        log.info("消息处理完毕");
        // 当消费者确认机制改为auto之后, 当监听接收消息时出现异常时会不停的发送消息, 如果服务宕机, 消息也不会消息
        // 但是如果接收到的消息的格式出现异常, 那么消息就会被直接reject, 消息会被删除
        throw new RuntimeException("测试消费者确认机制抛出的异常");
    }

    /**
     * rabbitMQ-work模型的使用: 让多个消费者绑定到一个队列,共同消费队列中的消息
     * 当一个队列有多个消费者消费时,可以加快处理消息的速度
     * 但是这时消息只会轮询给每个消费者,但是work模型需要实现能者多劳的情景
     * 加上perfetch配置,让消费者消费完一条消息才能继续消费消息,这样就可以实现能者多劳
     *
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

    /**
     * fanout广播
     *
     * @param message
     */
    @RabbitListener(queues = "fanout.queue1")
    private void listenerFanoutQueue2Message(String message) {
        log.info("收到了fanout.queue1的消息: {}", message);
    }

    /**
     * fanout广播
     *
     * @param message
     */
    @RabbitListener(queues = "fanout.queue2")
    private void listenerFanoutQueue1Message(String message) {
        log.info("收到了fanout.queue2的消息: {}", message);
    }

    /**
     * direct按需投放消息
     *
     * @param message
     */
    @RabbitListener(queues = "direct.queue1")
    private void listenerDirectQueue1Message(String message) {
        log.info("收到了direct.queue1的消息: {}", message);
    }

    /**
     * direct按需投放消息
     *
     * @param message
     */
    @RabbitListener(queues = "direct.queue2")
    private void listenerDirectQueue2Message(String message) {
        log.info("收到了direct.queue2的消息: {}", message);
    }

    /**
     * topic按需投放消息
     * 可以使用多个单词指定routing,使用.分隔,可以使用通配符指定routingKey的名称
     * #可以代表0个或者多个单词, *代表一个单词
     *
     * @param message
     */
    @RabbitListener(queues = "topic.queue1")
    private void listenerTopicQueue1Message(String message) {
        log.info("收到了topic.queue1的消息: {}", message);
    }

    /**
     * topic按需投放消息
     * 可以使用多个单词指定routing,使用.分隔,可以使用通配符指定routingKey的名称
     * #可以代表0个或者多个单词, *代表一个单词
     *
     * @param message
     */
    @RabbitListener(queues = "topic.queue2")
    private void listenerTopicQueue2Message(String message) {
        log.info("收到了topic.queue2的消息: {}", message);
    }


    /**
     * 使用注解创建队列交换机以及关系,顺便监听消息, 如果已经存在就不会再创建
     *
     * @param message
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue3", durable = "true"),
            exchange = @Exchange(name = "jackson.direct2", type = ExchangeTypes.DIRECT),
            key = {"red", "blue"}
    ))
    private void genExchangeAndQueue3AndBindingByListener(String message) {
        log.info("收到了direct.queue3的消息: {}", message);
    }

    /**
     * 使用注解创建队列交换机以及关系,顺便监听消息, 如果已经存在就不会再创建
     *
     * @param message
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue4", durable = "true"),
            exchange = @Exchange(name = "jackson.direct2", type = ExchangeTypes.DIRECT),
            key = {"red", "yellow"}
    ))
    private void genExchangeAndQueue4AndBindingByListener(String message) {
        log.info("收到了direct.queue4的消息: {}", message);
    }

    /**
     * 监听传递的对象消息数据
     *
     * @param message
     */
    @RabbitListener(queues = "object.queue")
    private void getObjectMessage(Map<String, Object> message) {
        log.info("收到了object.queue的消息: {}", message);
    }
}
