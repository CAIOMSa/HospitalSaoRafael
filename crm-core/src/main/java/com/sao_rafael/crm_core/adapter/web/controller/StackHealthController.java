package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.adapter.web.dto.ServiceStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import javax.sql.DataSource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class StackHealthController {

    private final RestTemplate restTemplate;
    private final DataSource dataSource;
    private final RedisConnectionFactory redisConnectionFactory;
    private final ConnectionFactory rabbitConnectionFactory;

    @GetMapping("/stack")
    public ResponseEntity<List<ServiceStatusDto>> stack() {
        List<ServiceStatusDto> list = new ArrayList<>();
        list.add(ServiceStatusDto.builder()
                .name("CRM Core")
                .url("http://localhost:8081")
                .status("online")
                .responseTimeMs(0L)
                .build());

        list.add(checkHttp("Python AI Service", "http://ai-service:8000/health"));
        list.add(checkHttp("Keycloak", "http://keycloak:8080/health/ready"));
        list.add(checkRabbit());
        list.add(checkRedis());
        list.add(checkPostgres());
        list.add(checkHttp("MinIO", "http://minio:9000/minio/health/live"));
        list.add(checkHttp("Prometheus", "http://prometheus:9090/-/healthy"));
        list.add(checkHttp("Grafana", "http://grafana:3000/api/health"));

        return ResponseEntity.ok(list);
    }

    private ServiceStatusDto checkHttp(String name, String url) {
        StopWatch sw = new StopWatch();
        try {
            sw.start();
            restTemplate.getForEntity(url, String.class);
            sw.stop();
            return ServiceStatusDto.builder()
                    .name(name)
                    .url(url)
                    .status("online")
                    .responseTimeMs(sw.getTotalTimeMillis())
                    .build();
        } catch (Exception ex) {
            if (sw.isRunning()) sw.stop();
            return ServiceStatusDto.builder()
                    .name(name)
                    .url(url)
                    .status("offline")
                    .responseTimeMs(sw.getTotalTimeMillis())
                    .error(ex.getMessage())
                    .build();
        }
    }

    private ServiceStatusDto checkPostgres() {
        StopWatch sw = new StopWatch();
        try (Connection ignored = dataSource.getConnection()) {
            sw.start();
            sw.stop();
            return ServiceStatusDto.builder()
                    .name("PostgreSQL")
                    .url("postgresql:5432")
                    .status("online")
                    .responseTimeMs(sw.getTotalTimeMillis())
                    .build();
        } catch (Exception ex) {
            if (sw.isRunning()) sw.stop();
            return ServiceStatusDto.builder()
                    .name("PostgreSQL")
                    .url("postgresql:5432")
                    .status("offline")
                    .responseTimeMs(sw.getTotalTimeMillis())
                    .error(ex.getMessage())
                    .build();
        }
    }

    private ServiceStatusDto checkRedis() {
        StopWatch sw = new StopWatch();
        try {
            sw.start();
            redisConnectionFactory.getConnection().ping();
            sw.stop();
            return ServiceStatusDto.builder()
                    .name("Redis")
                    .url("redis:6379")
                    .status("online")
                    .responseTimeMs(sw.getTotalTimeMillis())
                    .build();
        } catch (Exception ex) {
            if (sw.isRunning()) sw.stop();
            return ServiceStatusDto.builder()
                    .name("Redis")
                    .url("redis:6379")
                    .status("offline")
                    .responseTimeMs(sw.getTotalTimeMillis())
                    .error(ex.getMessage())
                    .build();
        }
    }

    private ServiceStatusDto checkRabbit() {
        StopWatch sw = new StopWatch();
        String url = "http://rabbitmq:15672/api/healthchecks/node";
        try {
            sw.start();
            rabbitConnectionFactory.createConnection().close();
            var headers = new org.springframework.http.HttpHeaders();
            String auth = "crm_user:crm_password";
            headers.add("Authorization", "Basic " + Base64.getEncoder().encodeToString(auth.getBytes()));
            var entity = new org.springframework.http.HttpEntity<String>(headers);
            restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, String.class);
            sw.stop();
            return ServiceStatusDto.builder()
                    .name("RabbitMQ")
                    .url(url)
                    .status("online")
                    .responseTimeMs(sw.getTotalTimeMillis())
                    .build();
        } catch (Exception ex) {
            if (sw.isRunning()) sw.stop();
            return ServiceStatusDto.builder()
                    .name("RabbitMQ")
                    .url(url)
                    .status("offline")
                    .responseTimeMs(sw.getTotalTimeMillis())
                    .error(ex.getMessage())
                    .build();
        }
    }
}
