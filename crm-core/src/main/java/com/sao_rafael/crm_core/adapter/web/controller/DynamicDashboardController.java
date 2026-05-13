package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.adapter.web.dto.request.Nlp2SqlQueryRequestDto;
import com.sao_rafael.crm_core.adapter.web.dto.response.DynamicDashboardConfigDto;
import com.sao_rafael.crm_core.adapter.web.dto.response.Nlp2SqlQueryResponseDto;
import com.sao_rafael.crm_core.application.service.DynamicDashboardService;
import com.sao_rafael.crm_core.application.service.Nlp2SqlBridgeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para Dashboard Dinâmico
 * Endpoints para configuração e dados do dashboard
 */
@RestController
@RequestMapping("/api/v1/dashboard/dynamic")
@CrossOrigin(origins = "*")
public class DynamicDashboardController {

    private final DynamicDashboardService dynamicDashboardService;
    private final Nlp2SqlBridgeService nlp2SqlBridgeService;

    public DynamicDashboardController(
        DynamicDashboardService dynamicDashboardService,
        Nlp2SqlBridgeService nlp2SqlBridgeService
    ) {
        this.dynamicDashboardService = dynamicDashboardService;
        this.nlp2SqlBridgeService = nlp2SqlBridgeService;
    }

    /**
     * GET /api/v1/dashboard/dynamic/config
     * Retorna a configuração completa do dashboard dinâmico com todos os widgets
     * 
     * @return DynamicDashboardConfigDto com widgets validados e seguros
     */
    @GetMapping("/config")
    public ResponseEntity<DynamicDashboardConfigDto> getDashboardConfig() {
        DynamicDashboardConfigDto config = dynamicDashboardService.getDashboardConfig();
        return ResponseEntity.ok(config);
    }

    @PostMapping("/nlp2sql")
    public ResponseEntity<Nlp2SqlQueryResponseDto> queryNlp2Sql(
        @Valid @RequestBody Nlp2SqlQueryRequestDto request
    ) {
        Nlp2SqlQueryResponseDto response = nlp2SqlBridgeService.query(request.getQuestion());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/nlp2sql/status")
    public ResponseEntity<java.util.Map<String, Object>> nlp2SqlStatus() {
        return ResponseEntity.ok(java.util.Map.of("enabled", nlp2SqlBridgeService.isEnabled()));
    }
}
