package com.sao_rafael.crm_core.infrastructure.messaging;

import com.sao_rafael.crm_core.infrastructure.config.RabbitMqConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiPromptServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private AiResponseRegistry responseRegistry;

    @InjectMocks
    private AiPromptService service;

    @Test
    void requestCompletionShouldSendMessageAndReturnRegistryResponse() {
        Map<String, Object> expected = Map.of("result", "ok");
        CompletableFuture<Map<String, Object>> completed = CompletableFuture.completedFuture(expected);
        when(responseRegistry.register(anyString())).thenReturn(completed);

        Map<String, Object> result = service.requestCompletion("Resumo paciente", Duration.ofSeconds(1));

        assertEquals(expected, result);

        ArgumentCaptor<Map<String, Object>> payloadCaptor = ArgumentCaptor.forClass(Map.class);
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMqConfig.EXCHANGE),
                eq(RabbitMqConfig.ROUTING_AI_PROMPT),
                payloadCaptor.capture(),
                any(MessagePostProcessor.class)
        );

        Map<String, Object> payload = payloadCaptor.getValue();
        assertEquals("Resumo paciente", payload.get("prompt"));
        assertNotNull(payload.get("requestId"));
        assertNotNull(payload.get("timestamp"));

        verify(responseRegistry).remove(anyString());
    }

    @Test
    void requestCompletionShouldThrowIllegalStateWhenTimeoutOccurs() {
        CompletableFuture<Map<String, Object>> neverCompletes = new CompletableFuture<>();
        when(responseRegistry.register(anyString())).thenReturn(neverCompletes);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.requestCompletion("Ping", Duration.ofMillis(5))
        );

        assertEquals("Timeout waiting for AI response", ex.getMessage());
        verify(responseRegistry).completeExceptionally(anyString(), isA(TimeoutException.class));
        verify(responseRegistry).remove(anyString());
    }

    @Test
    void requestCompletionShouldThrowIllegalStateWhenFutureFails() {
        CompletableFuture<Map<String, Object>> failed = new CompletableFuture<>();
        failed.completeExceptionally(new RuntimeException("broker down"));
        when(responseRegistry.register(anyString())).thenReturn(failed);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.requestCompletion("Ping", Duration.ofSeconds(1))
        );

        assertEquals("Error waiting for AI response", ex.getMessage());
        verify(responseRegistry).completeExceptionally(anyString(), any(Exception.class));
        verify(responseRegistry).remove(anyString());
    }
}
