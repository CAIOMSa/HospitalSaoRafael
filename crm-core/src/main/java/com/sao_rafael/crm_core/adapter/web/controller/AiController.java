package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.infrastructure.messaging.AiPromptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
@CrossOrigin(origins = "*")
public class AiController {

    private final AiPromptService aiPromptService;

    public AiController(AiPromptService aiPromptService) {
        this.aiPromptService = aiPromptService;
    }

    @PostMapping("/prompt")
    public ResponseEntity<Map<String, Object>> prompt(
            @RequestParam(defaultValue = "Ola") String prompt,
            @RequestParam(defaultValue = "20") int timeoutSeconds) {

        if (prompt == null || prompt.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Prompt nao pode ser vazio");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            Map<String, Object> response = aiPromptService.requestCompletion(
                    prompt,
                    Duration.ofSeconds(Math.max(1, timeoutSeconds))
            );

            Map<String, Object> payload = new HashMap<>();
            payload.put("status", "success");
            payload.put("prompt", prompt);
            payload.put("response", response);
            payload.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(payload);
        } catch (IllegalStateException ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", ex.getMessage());
            error.put("prompt", prompt);
            error.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(504).body(error);
        }
    }
}
