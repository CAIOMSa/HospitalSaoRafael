package com.sao_rafael.crm_core.infrastructure.messaging;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String TEST_QUEUE = "test-queue";
    public static final String TEST_EXCHANGE = "test-exchange";
    public static final String TEST_ROUTING_KEY = "test.routing.key";

    @Bean
    public Queue testQueue() {
        return QueueBuilder.durable(TEST_QUEUE).build();
    }
}
