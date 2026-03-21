package com.sao_rafael.crm_core.infrastructure.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class LokiLogUtils {

    private static final Logger logger = LoggerFactory.getLogger(LokiLogUtils.class);
    private final LokiPushService lokiPushService;
    private final ObjectMapper objectMapper;

    public LokiLogUtils(LokiPushService lokiPushService, ObjectMapper objectMapper) {
        this.lokiPushService = lokiPushService;
        this.objectMapper = objectMapper;
    }

    /**
     * Envia um log de erro da API para o Loki com contexto completo da requisição.
     *
     * @param method         método HTTP da requisição (GET, POST, etc.)
     * @param endpoint       caminho do endpoint chamado
     * @param requestPayload corpo da requisição, pode ser null
     * @param exception      exceção que ocorreu
     * @param response       resposta enviada ao cliente, pode ser null
     */
    public void logApiError(String method, String endpoint, Object requestPayload,
                           Exception exception, Object response) {
        try {
            Map<String, Object> logData = new HashMap<>();
            logData.put("timestamp", Instant.now().toString());
            logData.put("level", "ERROR");
            logData.put("method", method);
            logData.put("endpoint", endpoint);
            logData.put("error_type", exception.getClass().getSimpleName());
            logData.put("error_message", exception.getMessage());
            logData.put("request_payload", requestPayload != null ? objectMapper.writeValueAsString(requestPayload) : "null");
            logData.put("response", response != null ? objectMapper.writeValueAsString(response) : "null");

            String jsonLog = objectMapper.writeValueAsString(logData);
            
            Map<String, String> labels = new HashMap<>();
            labels.put("app", "crm-core");
            labels.put("level", "error");
            labels.put("type", "api_error");
            labels.put("endpoint", endpoint);
            
            lokiPushService.pushLog(jsonLog, labels);
            logger.error("[API Error] {} {} - {}: {}", method, endpoint, exception.getClass().getSimpleName(), exception.getMessage());
        } catch (Exception e) {
            logger.error("[LokiLogUtils] Falha ao enviar log de erro: {}", e.getMessage());
        }
    }

    /**
     * Envia um log de sucesso da API para o Loki com request e response serializados.
     *
     * @param method         método HTTP da requisição
     * @param endpoint       caminho do endpoint chamado
     * @param requestPayload corpo da requisição, pode ser null
     * @param response       resposta enviada ao cliente, pode ser null
     * @param statusCode     código HTTP de status da resposta
     */
    public void logApiSuccess(String method, String endpoint, Object requestPayload,
                             Object response, int statusCode) {
        try {
            Map<String, Object> logData = new HashMap<>();
            logData.put("timestamp", Instant.now().toString());
            logData.put("level", "INFO");
            logData.put("method", method);
            logData.put("endpoint", endpoint);
            logData.put("status_code", statusCode);
            logData.put("request_payload", requestPayload != null ? objectMapper.writeValueAsString(requestPayload) : "null");
            logData.put("response", response != null ? objectMapper.writeValueAsString(response) : "null");

            String jsonLog = objectMapper.writeValueAsString(logData);
            
            Map<String, String> labels = new HashMap<>();
            labels.put("app", "crm-core");
            labels.put("level", "info");
            labels.put("type", "api_success");
            labels.put("endpoint", endpoint);
            
            lokiPushService.pushLog(jsonLog, labels);
            logger.info("[API Success] {} {} - Status: {}", method, endpoint, statusCode);
        } catch (Exception e) {
            logger.error("[LokiLogUtils] Falha ao enviar log de sucesso: {}", e.getMessage());
        }
    }

    /**
     * Envia um log de debug da requisição recebida para o Loki.
     *
     * @param method         método HTTP da requisição
     * @param endpoint       caminho do endpoint chamado
     * @param requestPayload corpo da requisição, pode ser null
     */
    public void logApiRequest(String method, String endpoint, Object requestPayload) {
        try {
            Map<String, Object> logData = new HashMap<>();
            logData.put("timestamp", Instant.now().toString());
            logData.put("level", "DEBUG");
            logData.put("method", method);
            logData.put("endpoint", endpoint);
            logData.put("request_payload", requestPayload != null ? objectMapper.writeValueAsString(requestPayload) : "null");

            String jsonLog = objectMapper.writeValueAsString(logData);
            
            Map<String, String> labels = new HashMap<>();
            labels.put("app", "crm-core");
            labels.put("level", "debug");
            labels.put("type", "api_request");
            labels.put("endpoint", endpoint);
            
            lokiPushService.pushLog(jsonLog, labels);
            logger.debug("[API Request] {} {}", method, endpoint);
        } catch (Exception e) {
            logger.error("[LokiLogUtils] Falha ao enviar log de request: {}", e.getMessage());
        }
    }
}
