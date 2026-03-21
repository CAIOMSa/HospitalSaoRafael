package com.sao_rafael.crm_core.infrastructure.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sao_rafael.crm_core.infrastructure.logging.LokiLogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Component
public class TestMessageProducer {

    private static final Logger logger = LoggerFactory.getLogger(TestMessageProducer.class);
    
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final LokiLogUtils lokiLogUtils;

    public TestMessageProducer(RabbitTemplate rabbitTemplate, 
                              ObjectMapper objectMapper,
                              LokiLogUtils lokiLogUtils) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.lokiLogUtils = lokiLogUtils;
    }

    public void sendTestMessage(String message) {
        try {
            logger.info("[RabbitMQ] Preparing to send test message");
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("message", message);
            payload.put("timestamp", System.currentTimeMillis());
            payload.put("source", "crm-core-backend");

            String jsonMessage = objectMapper.writeValueAsString(payload);
            
            rabbitTemplate.convertAndSend(RabbitMQConfig.TEST_QUEUE, jsonMessage);
            Map<String, String> labels = new HashMap<>();
            labels.put("app", "crm-core");
            labels.put("level", "info");
            labels.put("type", "rabbitmq_message");
            labels.put("queue", RabbitMQConfig.TEST_QUEUE);
            
            Map<String, Object> logData = new HashMap<>();
            logData.put("event", "message_sent");
            logData.put("queue", RabbitMQConfig.TEST_QUEUE);
            logData.put("payload", payload);
            
            lokiLogUtils.logApiSuccess("RabbitMQ", RabbitMQConfig.TEST_QUEUE, payload, 
                                      "Message sent successfully", 200);
            
            logger.info("[RabbitMQ] Message sent successfully to queue: {}", RabbitMQConfig.TEST_QUEUE);
        } catch (Exception e) {
            logger.error("[RabbitMQ] Error sending message: {}", e.getMessage(), e);
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("event", "message_send_error");
            errorData.put("queue", RabbitMQConfig.TEST_QUEUE);
            
            lokiLogUtils.logApiError("RabbitMQ", RabbitMQConfig.TEST_QUEUE, null, e, errorData);
            
            throw new RuntimeException("Error sending message to RabbitMQ", e);
        }
    }
}
