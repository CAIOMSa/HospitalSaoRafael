package com.sao_rafael.crm_core.infrastructure.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LokiPushService {

    private static final Logger logger = LoggerFactory.getLogger(LokiPushService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String lokiUrl;

    public LokiPushService(ObjectMapper objectMapper,
                           @Value("${loki.url:http://localhost:3100}") String lokiUrl) {
        this.objectMapper = objectMapper;
        this.lokiUrl = lokiUrl;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Envia uma mensagem de teste ao Loki com labels padrão de tipo "test".
     *
     * @param message conteúdo da mensagem a ser enviado
     */
    public void pushTestLog(String message) {
        Map<String, String> labels = new HashMap<>();
        labels.put("app", "crm-core");
        labels.put("level", "info");
        labels.put("type", "test");
        pushLog(message, labels);
    }

    /**
     * Envia uma entrada de log ao Loki via HTTP Push API.
     * Serializa a mensagem no formato de stream esperado pelo Loki.
     *
     * @param message conteúdo da mensagem de log
     * @param labels  mapa de labels para categorizar o log no Loki
     * @throws RuntimeException se o envio falhar
     */
    public void pushLog(String message, Map<String, String> labels) {
        try {
            String timestampNs = String.valueOf(Instant.now().toEpochMilli() * 1_000_000);

            Map<String, Object> stream = new HashMap<>();
            stream.putAll(labels);

            Map<String, Object> streamEntry = new HashMap<>();
            streamEntry.put("stream", stream);
            streamEntry.put("values", List.of(List.of(timestampNs, message)));

            Map<String, Object> payload = new HashMap<>();
            payload.put("streams", List.of(streamEntry));

            ResponseEntity<String> response = restTemplate.postForEntity(
                lokiUrl + "/loki/api/v1/push",
                payload,
                String.class
            );

            logger.info("[Loki] Log enviado com status: {}", response.getStatusCode());
        } catch (Exception e) {
            logger.error("[Loki] Falha ao enviar log: {}", e.getMessage());
            throw new RuntimeException("Erro ao enviar log para Loki", e);
        }
    }
}
