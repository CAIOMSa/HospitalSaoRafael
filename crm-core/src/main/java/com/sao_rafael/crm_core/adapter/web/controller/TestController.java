package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.infrastructure.logging.LokiPushService;
import com.sao_rafael.crm_core.infrastructure.messaging.TestMessageProducer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/testes")
@CrossOrigin(origins = "*")
public class TestController {

    private final TestMessageProducer messageProducer;
    private final LokiPushService lokiPushService;
    private final MeterRegistry meterRegistry;
    private Counter testCounter;

    public TestController(TestMessageProducer messageProducer,
                          LokiPushService lokiPushService,
                          MeterRegistry meterRegistry) {
        this.messageProducer = messageProducer;
        this.lokiPushService = lokiPushService;
        this.meterRegistry = meterRegistry;
        
        this.testCounter = Counter.builder("crm.test.counter")
            .description("Contador de testes executados")
            .register(meterRegistry);
    }

    @PostMapping("/loki")
    public ResponseEntity<Map<String, Object>> testLoki(
            @RequestParam(defaultValue = "Log de teste do backend") String message) {
        try {
            lokiPushService.pushTestLog(message);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Log enviado para Loki com sucesso!");
            response.put("sentMessage", message);
            response.put("loki_url", "http://localhost:3100");
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Erro ao enviar log: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/rabbitmq")
    public ResponseEntity<Map<String, Object>> testRabbitMQ(
            @RequestParam(defaultValue = "Mensagem de teste do backend") String message) {
        try {
            messageProducer.sendTestMessage(message);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Mensagem enviada para RabbitMQ com sucesso!");
            response.put("sentMessage", message);
            response.put("queue", "test-queue");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Erro ao enviar mensagem: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/prometheus")
    public ResponseEntity<Map<String, Object>> testPrometheus(
            @RequestParam(defaultValue = "Teste de mÃ©trica") String label) {
        try {
            testCounter.increment();
            
            double testMetricValue = Math.random() * 100;
            meterRegistry.gauge("crm.test.metric", testMetricValue);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "MÃ©trica registrada no Prometheus com sucesso!");
            response.put("metric", "crm.test.counter");
            response.put("gauge", "crm.test.metric");
            response.put("gaugeValue", testMetricValue);
            response.put("timestamp", System.currentTimeMillis());
            response.put("prometheus_url", "http://localhost:9090");
            response.put("grafana_url", "http://localhost:3001");
            response.put("metrics_endpoint", "http://localhost:8081/actuator/prometheus");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Erro ao registrar mÃ©trica: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> testStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("rabbitmq_queue", "test-queue");
        status.put("rabbitmq_status", "connected");
        status.put("prometheus_metrics", new String[]{
            "crm.test.counter",
            "crm.test.metric"
        });
        status.put("prometheus_url", "http://localhost:9090");
        status.put("loki_url", "http://localhost:3100");
        status.put("grafana_url", "http://localhost:3001");
        status.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(status);
    }
}
