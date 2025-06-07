package com.jackson.bean;

import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.rabbit.connection.CorrelationData;

@Setter
@Getter
public class MyCorrelationData extends CorrelationData {
    private String exchange;
    private String routingKey;
    private Object message;
    private Integer retryCount;

    public MyCorrelationData(String id, String exchange, String routingKey, Object message, Integer retryCount) {
        super(id);
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.message = message;
        this.retryCount = retryCount;
    }

}
