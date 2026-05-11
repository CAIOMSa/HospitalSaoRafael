package com.sao_rafael.crm_core.adapter.web.dto;

import java.math.BigDecimal;
import java.util.List;

public record FinanceAnalyticsResponseDto(
        Long cirurgiasRealizadas,
        Long cirurgiasAgendadas,
        BigDecimal faturamentoEstimado,
        BigDecimal custoEstimado,
        BigDecimal margemEstimada,
        BigDecimal folhaMensalEstimada,
        List<FinanceAnalyticsItemDto> procedimentos,
        List<FinancePayrollItemDto> folha
) {
}
