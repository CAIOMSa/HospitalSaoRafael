package com.sao_rafael.crm_core.infrastructure.messaging;

import com.sao_rafael.crm_core.infrastructure.config.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class AiPromptService {

    private static final Logger logger = LoggerFactory.getLogger(AiPromptService.class);

    private final RabbitTemplate rabbitTemplate;
    private final AiResponseRegistry responseRegistry;

    public AiPromptService(RabbitTemplate rabbitTemplate, AiResponseRegistry responseRegistry) {
        this.rabbitTemplate = rabbitTemplate;
        this.responseRegistry = responseRegistry;
    }

    public Map<String, Object> requestCompletion(String prompt, Duration timeout) {
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<Map<String, Object>> future = responseRegistry.register(requestId);

        Map<String, Object> payload = new HashMap<>();
        payload.put("requestId", requestId);
        payload.put("prompt", prompt);
        payload.put("timestamp", System.currentTimeMillis());

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE,
                RabbitMqConfig.ROUTING_AI_PROMPT,
                payload,
                message -> {
                    message.getMessageProperties().setCorrelationId(requestId);
                    message.getMessageProperties().setReplyTo(RabbitMqConfig.QUEUE_AI_RESPONSE);
                    return message;
                }
        );

        try {
            return future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException ex) {
            responseRegistry.completeExceptionally(requestId, ex);
            logger.warn("AI response timeout for requestId={}", requestId);
            throw new IllegalStateException("Timeout waiting for AI response");
        } catch (Exception ex) {
            responseRegistry.completeExceptionally(requestId, ex);
            logger.error("AI response error for requestId={}", requestId, ex);
            throw new IllegalStateException("Error waiting for AI response");
        } finally {
            responseRegistry.remove(requestId);
        }
    }
}
