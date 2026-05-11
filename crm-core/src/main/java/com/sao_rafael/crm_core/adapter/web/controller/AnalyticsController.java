package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.adapter.web.dto.FinanceAnalyticsResponseDto;
import com.sao_rafael.crm_core.adapter.web.dto.LeadsAnalyticsResponseDto;
import com.sao_rafael.crm_core.application.service.AnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/financeiro")
    public ResponseEntity<FinanceAnalyticsResponseDto> getFinancialAnalytics(
            @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(analyticsService.getFinanceAnalytics(from, to));
    }

    @GetMapping("/leads")
    public ResponseEntity<LeadsAnalyticsResponseDto> getLeadsAnalytics(
            @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(analyticsService.getLeadsAnalytics(from, to));
    }
}
