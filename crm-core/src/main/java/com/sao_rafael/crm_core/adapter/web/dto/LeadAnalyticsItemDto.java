package com.sao_rafael.crm_core.adapter.web.dto;

import java.math.BigDecimal;

public record LeadAnalyticsItemDto(
        Long pacienteId,
        String nome,
        String telefone,
        String email,
        String canal,
        String etapa,
        BigDecimal probabilidade,
        BigDecimal valorPotencial,
        BigDecimal valorPonderado
) {
}
