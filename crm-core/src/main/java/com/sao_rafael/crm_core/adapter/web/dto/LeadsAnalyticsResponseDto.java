package com.sao_rafael.crm_core.adapter.web.dto;

import java.math.BigDecimal;
import java.util.List;

public record LeadsAnalyticsResponseDto(
        Long totalLeads,
        Long novos,
        Long contato,
        Long triagem,
        Long qualificados,
        BigDecimal pipelineBruto,
        BigDecimal pipelinePonderado,
        List<LeadAnalyticsItemDto> leads
) {
}
