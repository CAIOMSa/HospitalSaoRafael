package com.sao_rafael.crm_core.adapter.web.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record LeadsAnalyticsResponseDto(
        Long totalLeads,
        Long novos,
        Long contato,
        Long triagem,
        Long qualificados,
        Long slaAtrasados,
        Long semFollowUp,
        BigDecimal pipelineBruto,
        BigDecimal pipelinePonderado,
        List<LeadConversionItemDto> conversoesFunil,
        Map<String, List<LeadAnalyticsItemDto>> kanban,
        List<LeadAnalyticsItemDto> leads
) {
}
