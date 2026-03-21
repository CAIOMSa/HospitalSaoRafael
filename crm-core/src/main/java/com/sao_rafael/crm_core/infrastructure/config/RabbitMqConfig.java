package com.sao_rafael.crm_core.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE = "crm.events";
    public static final String QUEUE_CUSTOMER_CREATED = "customer.created.queue";
    public static final String QUEUE_CUSTOMER_UPDATED = "customer.updated.queue";
    public static final String QUEUE_CUSTOMER_DELETED = "customer.deleted.queue";
    public static final String QUEUE_AI_PROMPT = "ai.prompt.queue";
    public static final String QUEUE_AI_RESPONSE = "ai.response.queue";
    public static final String ROUTING_AI_PROMPT = "ai.prompt";
    public static final String ROUTING_AI_RESPONSE = "ai.response";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue customerCreatedQueue() {
        return new Queue(QUEUE_CUSTOMER_CREATED, true);
    }

    @Bean
    public Queue customerUpdatedQueue() {
        return new Queue(QUEUE_CUSTOMER_UPDATED, true);
    }

    @Bean
    public Queue customerDeletedQueue() {
        return new Queue(QUEUE_CUSTOMER_DELETED, true);
    }

    @Bean
    public Queue aiPromptQueue() {
        return new Queue(QUEUE_AI_PROMPT, true);
    }

    @Bean
    public Queue aiResponseQueue() {
        return new Queue(QUEUE_AI_RESPONSE, true);
    }

    @Bean
    public Binding customerCreatedBinding(Queue customerCreatedQueue, TopicExchange exchange) {
        return BindingBuilder.bind(customerCreatedQueue).to(exchange).with("customer.created");
    }

    @Bean
    public Binding customerUpdatedBinding(Queue customerUpdatedQueue, TopicExchange exchange) {
        return BindingBuilder.bind(customerUpdatedQueue).to(exchange).with("customer.updated");
    }

    @Bean
    public Binding customerDeletedBinding(Queue customerDeletedQueue, TopicExchange exchange) {
        return BindingBuilder.bind(customerDeletedQueue).to(exchange).with("customer.deleted");
    }

    @Bean
    public Binding aiPromptBinding(Queue aiPromptQueue, TopicExchange exchange) {
        return BindingBuilder.bind(aiPromptQueue).to(exchange).with(ROUTING_AI_PROMPT);
    }

    @Bean
    public Binding aiResponseBinding(Queue aiResponseQueue, TopicExchange exchange) {
        return BindingBuilder.bind(aiResponseQueue).to(exchange).with(ROUTING_AI_RESPONSE);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
