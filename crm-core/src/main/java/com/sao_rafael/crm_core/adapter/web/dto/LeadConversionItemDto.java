package com.sao_rafael.crm_core.adapter.web.dto;

import java.math.BigDecimal;

public record LeadConversionItemDto(
        String etapaDe,
        String etapaPara,
        BigDecimal conversaoPercentual
) {
}
