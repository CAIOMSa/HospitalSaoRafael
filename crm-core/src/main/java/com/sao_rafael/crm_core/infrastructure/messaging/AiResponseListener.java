package com.sao_rafael.crm_core.infrastructure.messaging;

import com.sao_rafael.crm_core.infrastructure.config.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AiResponseListener {

    private static final Logger logger = LoggerFactory.getLogger(AiResponseListener.class);

    private final AiResponseRegistry responseRegistry;

    public AiResponseListener(AiResponseRegistry responseRegistry) {
        this.responseRegistry = responseRegistry;
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE_AI_RESPONSE)
    public void handleResponse(Map<String, Object> payload, Message message) {
        String requestId = null;
        if (payload != null && payload.get("requestId") != null) {
            requestId = payload.get("requestId").toString();
        }
        if (requestId == null && message.getMessageProperties() != null) {
            requestId = message.getMessageProperties().getCorrelationId();
        }
        if (requestId == null) {
            logger.warn("AI response missing requestId/correlationId");
            return;
        }

        responseRegistry.complete(requestId, payload);
    }
}
