package com.sao_rafael.crm_core.adapter.web.dto;

import java.math.BigDecimal;

public record FinanceAnalyticsItemDto(
        String procedimento,
        Long realizadas,
        Long agendadas,
        BigDecimal receitaEstimada,
        BigDecimal custoEstimado,
        BigDecimal margemEstimada
) {
}
