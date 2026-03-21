package com.sao_rafael.crm_core.infrastructure.messaging;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AiResponseRegistry {

    private final ConcurrentHashMap<String, CompletableFuture<Map<String, Object>>> pending = new ConcurrentHashMap<>();

    public CompletableFuture<Map<String, Object>> register(String requestId) {
        CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
        pending.put(requestId, future);
        return future;
    }

    public void complete(String requestId, Map<String, Object> payload) {
        CompletableFuture<Map<String, Object>> future = pending.remove(requestId);
        if (future != null) {
            future.complete(payload);
        }
    }

    public void completeExceptionally(String requestId, Throwable error) {
        CompletableFuture<Map<String, Object>> future = pending.remove(requestId);
        if (future != null) {
            future.completeExceptionally(error);
        }
    }

    public void remove(String requestId) {
        pending.remove(requestId);
    }
}
