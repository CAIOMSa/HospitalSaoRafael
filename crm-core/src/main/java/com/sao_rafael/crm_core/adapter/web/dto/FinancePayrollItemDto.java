package com.sao_rafael.crm_core.adapter.web.dto;

import java.math.BigDecimal;

public record FinancePayrollItemDto(
        Long funcionarioId,
        String funcionario,
        String cargo,
        String departamento,
        BigDecimal salarioEstimado
) {
}
