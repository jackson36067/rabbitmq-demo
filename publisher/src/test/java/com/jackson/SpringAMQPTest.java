package com.jackson;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

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

    /**
     * 测试work模型: 多消费者消费消息
     *
     * @throws InterruptedException
     */
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

    /**
     * 通过exchange交换机广播消息 - 类型: fanout
     */
    @Test
    public void testSendFanoutMessage() {
        // 交换机名称, 交换机类型一定要试fanout
        String exchange = "jackson.fanout";
        // 信息
        String message = "hello every queue";
        // 广播时queue队列名称给""或者null
        rabbitTemplate.convertAndSend(exchange, "", message);
    }

    /**
     * 通过exchange交换机按需发送消息 - 类型: direct
     * 根据exchange交换机与queue绑定的routing实现按需路由消息给队列
     */
    @Test
    public void testSendDirectMessage() {
        // 交换机名称, 交换机类型一定要试direct
        String exchange = "jackson.direct";
        // routing名称 -> 通过这个获取需要路由的消息的队列
        // String routingKey = "red";
        // String routingKey = "blue";
        String routingKey = "yellow";
        // 信息
        String message = "yellow是什么颜色: 黄色";
        // 广播时queue队列名称给""或者null
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }

    /**
     * 通过exchange交换机按需发送消息 - 类型: topic
     * 根据exchange交换机与queue绑定的routing实现按需路由消息给队列
     * 与direct不同的是direct只可以使用一个单词作为routingKey,但是topic可以使用多个单词并且可以使用通配符进行匹配routingKey
     */
    @Test
    public void testSendTopicMessage() {
        // 交换机名称, 交换机类型一定要试direct
        String exchange = "jackson.topic";
        // routing名称 -> 通过这个获取需要路由的消息的队列
        // String routingKey = "china.weather";
        // String routingKey = "china.news";
        String routingKey = "japan.news";
        // 信息
        String message = "日本cs,新闻都是负面的";
        // 广播时queue队列名称给""或者null
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }

    // 发送map类型的消息
    @Test
    public void testSendMapMessage() {
        Map<String, Object> message = new HashMap<>();
        message.put("name", "jackson");
        message.put("age", 18);
        // 这里会使用默认的消息转换器去转换对象值, 默认的消息转换器是通过序列化对象,最终发送的消息是序列化后的结果
        // 可以通过在消费者以及生产者中声明自己的消息转换器即可修改消息转换器类型,可以使用json格式的消息转换器
        rabbitTemplate.convertAndSend("object.queue", message);
    }
}