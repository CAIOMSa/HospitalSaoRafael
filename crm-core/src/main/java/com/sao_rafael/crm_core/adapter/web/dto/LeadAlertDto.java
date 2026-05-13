package com.sao_rafael.crm_core.adapter.web.dto;

public record LeadAlertDto(
        String tipo,
        String severidade,
        String mensagem
) {
}
