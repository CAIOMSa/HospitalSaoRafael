package com.sao_rafael.crm_core.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sao_rafael.crm_core.adapter.web.dto.response.Nlp2SqlQueryResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class Nlp2SqlBridgeService {

    private static final Logger log = LoggerFactory.getLogger(Nlp2SqlBridgeService.class);

    private static final Set<String> DANGEROUS_PROMPT_TERMS = Set.of(
        "drop", "truncate", "delete", "update", "insert", "alter", "grant", "revoke", "pg_", "information_schema"
    );

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final boolean nlp2sqlEnabled;
    private final String aiServiceBaseUrl;

    public Nlp2SqlBridgeService(
        RestTemplate restTemplate,
        ObjectMapper objectMapper,
        @Value("${features.nlp2sql.enabled:true}") boolean nlp2sqlEnabled,
        @Value("${features.nlp2sql.ai-service-url:http://localhost:8000}") String aiServiceBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.nlp2sqlEnabled = nlp2sqlEnabled;
        this.aiServiceBaseUrl = aiServiceBaseUrl;
    }

    public boolean isEnabled() {
        return nlp2sqlEnabled;
    }

    public Nlp2SqlQueryResponseDto query(String question) {
        if (!nlp2sqlEnabled) {
            return disabledResponse();
        }

        String normalizedQuestion = normalizeQuestion(question);
        if (normalizedQuestion.isBlank()) {
            return blockedResponse("Pergunta vazia");
        }

        for (String term : DANGEROUS_PROMPT_TERMS) {
            if (normalizedQuestion.contains(term)) {
                return blockedResponse("Pergunta bloqueada por guardrail de segurança");
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("question", question);

        List<String> candidateBaseUrls = buildCandidateBaseUrls();

        for (String candidateBaseUrl : candidateBaseUrls) {
            try {
                ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    candidateBaseUrl + "/api/v1/analytics/nlp2sql",
                    HttpMethod.POST,
                    new HttpEntity<>(payload, headers),
                    new ParameterizedTypeReference<>() {
                    }
                );

                return mapResponse(response.getBody());
            } catch (HttpStatusCodeException ex) {
                log.warn("NLP2SQL bridge HTTP {} from {}", ex.getStatusCode(), candidateBaseUrl);
                return mapHttpErrorResponse(ex);
            } catch (RestClientException ex) {
                log.warn("NLP2SQL bridge communication failed for {}: {}", candidateBaseUrl, ex.getMessage());
            }
        }

        try {
            Map<String, Object> diagnostics = new LinkedHashMap<>();
            diagnostics.put("question", question);
            diagnostics.put("candidate_base_urls", candidateBaseUrls);
            log.error("NLP2SQL bridge unavailable. Diagnostics={}", objectMapper.writeValueAsString(diagnostics));
        } catch (RestClientException ex) {
            log.error("Falha ao serializar diagnóstico do bridge NLP2SQL", ex);
        } catch (Exception ex) {
            log.error("Falha inesperada ao montar diagnóstico do bridge NLP2SQL", ex);
        }

        Nlp2SqlQueryResponseDto dto = new Nlp2SqlQueryResponseDto();
        dto.setSuccess(false);
        dto.setAnswer("Módulo NLP2SQL temporariamente indisponível.");
        dto.setSource("bridge-unavailable");
        dto.setColumns(List.of());
        dto.setRows(List.of());
        dto.setRowCount(0);
        dto.setGuardrail(new Nlp2SqlQueryResponseDto.GuardrailDto(false, "Falha de comunicação com ai-service"));
        return dto;
    }

    private List<String> buildCandidateBaseUrls() {
        List<String> candidates = new ArrayList<>();
        candidates.add(aiServiceBaseUrl);

        if (aiServiceBaseUrl.contains("localhost") || aiServiceBaseUrl.contains("127.0.0.1")) {
            candidates.add("http://ai-service:8000");
            candidates.add("http://host.docker.internal:8000");
        }

        return candidates.stream().distinct().toList();
    }

    private Nlp2SqlQueryResponseDto mapHttpErrorResponse(HttpStatusCodeException ex) {
        String responseBody = ex.getResponseBodyAsString();

        if (ex.getStatusCode().value() == 422) {
            try {
                Map<String, Object> body = objectMapper.readValue(
                    responseBody,
                    new TypeReference<Map<String, Object>>() {}
                );

                Nlp2SqlQueryResponseDto dto = new Nlp2SqlQueryResponseDto();
                dto.setSuccess(false);
                dto.setAnswer((String) body.getOrDefault("answer", "Pergunta inválida para NLP2SQL (422)"));
                dto.setGeneratedSql((String) body.getOrDefault("generated_sql", ""));
                dto.setSource((String) body.getOrDefault("source", "guardrail"));

                List<String> columns = objectMapper.convertValue(
                    body.getOrDefault("columns", List.of()),
                    new TypeReference<List<String>>() {}
                );
                dto.setColumns(columns);

                List<Map<String, Object>> rows = objectMapper.convertValue(
                    body.getOrDefault("rows", List.of()),
                    new TypeReference<List<Map<String, Object>>>() {}
                );
                dto.setRows(rows);
                dto.setRowCount(((Number) body.getOrDefault("row_count", rows.size())).intValue());

                Map<String, Object> guardrailMap = objectMapper.convertValue(
                    body.getOrDefault("guardrail", Map.of("blocked", true, "reason", "Pergunta inválida para NLP2SQL (422)")),
                    new TypeReference<Map<String, Object>>() {}
                );

                boolean blocked = Boolean.TRUE.equals(guardrailMap.get("blocked"));
                String reason = String.valueOf(guardrailMap.getOrDefault("reason", "Pergunta inválida para NLP2SQL (422)"));
                dto.setGuardrail(new Nlp2SqlQueryResponseDto.GuardrailDto(blocked, reason));

                return dto;
            } catch (Exception parseEx) {
                log.warn("Falha ao parsear corpo 422 do ai-service: {}", parseEx.getMessage());
                return blockedResponse("Pergunta inválida para NLP2SQL (422)");
            }
        }

        Nlp2SqlQueryResponseDto dto = new Nlp2SqlQueryResponseDto();
        dto.setSuccess(false);
        dto.setAnswer("Falha ao consultar NLP2SQL.");
        dto.setGeneratedSql("");
        dto.setColumns(List.of());
        dto.setRows(List.of());
        dto.setRowCount(0);
        dto.setSource("bridge-http-error");
        dto.setGuardrail(
            new Nlp2SqlQueryResponseDto.GuardrailDto(
                false,
                "HTTP " + ex.getStatusCode().value() + (responseBody == null || responseBody.isBlank() ? "" : ": " + responseBody)
            )
        );
        return dto;
    }

    private Nlp2SqlQueryResponseDto mapResponse(Map<String, Object> body) {
        if (body == null) {
            return blockedResponse("Resposta vazia do ai-service");
        }

        Nlp2SqlQueryResponseDto dto = new Nlp2SqlQueryResponseDto();
        dto.setSuccess(Boolean.TRUE.equals(body.get("success")));
        dto.setAnswer((String) body.getOrDefault("answer", "Sem resposta"));
        dto.setGeneratedSql((String) body.getOrDefault("generated_sql", ""));
        dto.setSource((String) body.getOrDefault("source", "ai-service"));

        List<String> columns = objectMapper.convertValue(
            body.getOrDefault("columns", List.of()),
            new TypeReference<List<String>>() {}
        );
        dto.setColumns(columns);

        List<Map<String, Object>> rows = objectMapper.convertValue(
            body.getOrDefault("rows", List.of()),
            new TypeReference<List<Map<String, Object>>>() {}
        );
        dto.setRows(rows);
        dto.setRowCount(((Number) body.getOrDefault("row_count", rows.size())).intValue());

        Map<String, Object> insightsPlan = objectMapper.convertValue(
            body.getOrDefault("insights_plan", Map.of()),
            new TypeReference<Map<String, Object>>() {}
        );
        dto.setInsightsPlan(insightsPlan);

        Map<String, Object> guardrailMap = objectMapper.convertValue(
            body.getOrDefault("guardrail", Map.of("blocked", false, "reason", "")),
            new TypeReference<Map<String, Object>>() {}
        );

        boolean blocked = Boolean.TRUE.equals(guardrailMap.get("blocked"));
        String reason = String.valueOf(guardrailMap.getOrDefault("reason", ""));
        dto.setGuardrail(new Nlp2SqlQueryResponseDto.GuardrailDto(blocked, reason));

        return dto;
    }

    private String normalizeQuestion(String question) {
        if (question == null) {
            return "";
        }
        return question.trim().toLowerCase();
    }

    private Nlp2SqlQueryResponseDto blockedResponse(String reason) {
        Nlp2SqlQueryResponseDto dto = new Nlp2SqlQueryResponseDto();
        dto.setSuccess(false);
        dto.setAnswer("Consulta bloqueada por política de segurança.");
        dto.setGeneratedSql("");
        dto.setColumns(new ArrayList<>());
        dto.setRows(new ArrayList<>());
        dto.setRowCount(0);
        dto.setSource("guardrail");
        dto.setGuardrail(new Nlp2SqlQueryResponseDto.GuardrailDto(true, reason));
        return dto;
    }

    private Nlp2SqlQueryResponseDto disabledResponse() {
        Nlp2SqlQueryResponseDto dto = new Nlp2SqlQueryResponseDto();
        dto.setSuccess(false);
        dto.setAnswer("Módulo NLP2SQL desligado no momento.");
        dto.setGeneratedSql("");
        dto.setColumns(List.of());
        dto.setRows(List.of());
        dto.setRowCount(0);
        dto.setSource("disabled");
        dto.setGuardrail(new Nlp2SqlQueryResponseDto.GuardrailDto(true, "features.nlp2sql.enabled=false"));
        return dto;
    }
}
