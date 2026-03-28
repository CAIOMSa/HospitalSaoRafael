package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.infrastructure.messaging.AiPromptService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiControllerTest {

    @Mock
    private AiPromptService aiPromptService;

    @InjectMocks
    private AiController aiController;

    @Test
    void promptShouldReturnBadRequestWhenPromptIsBlank() {
        ResponseEntity<Map<String, Object>> response = aiController.prompt("   ", 10);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("error", response.getBody().get("status"));
        assertEquals("Prompt nao pode ser vazio", response.getBody().get("message"));
        verifyNoInteractions(aiPromptService);
    }

    @Test
    void promptShouldClampTimeoutAndReturnSuccessPayload() {
        Map<String, Object> serviceResponse = Map.of("answer", "ok");
        when(aiPromptService.requestCompletion(eq("Oi"), any(Duration.class))).thenReturn(serviceResponse);

        ResponseEntity<Map<String, Object>> response = aiController.prompt("Oi", 0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Oi", response.getBody().get("prompt"));
        assertEquals(serviceResponse, response.getBody().get("response"));
        assertTrue(response.getBody().containsKey("timestamp"));

        ArgumentCaptor<Duration> durationCaptor = ArgumentCaptor.forClass(Duration.class);
        verify(aiPromptService).requestCompletion(eq("Oi"), durationCaptor.capture());
        assertEquals(Duration.ofSeconds(1), durationCaptor.getValue());
    }

    @Test
    void promptShouldReturnGatewayTimeoutWhenServiceTimesOut() {
        when(aiPromptService.requestCompletion(eq("Pergunta"), any(Duration.class)))
                .thenThrow(new IllegalStateException("Timeout waiting for AI response"));

        ResponseEntity<Map<String, Object>> response = aiController.prompt("Pergunta", 5);

        assertEquals(HttpStatus.GATEWAY_TIMEOUT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("error", response.getBody().get("status"));
        assertEquals("Timeout waiting for AI response", response.getBody().get("message"));
        assertEquals("Pergunta", response.getBody().get("prompt"));
        assertTrue(response.getBody().containsKey("timestamp"));
    }
}
