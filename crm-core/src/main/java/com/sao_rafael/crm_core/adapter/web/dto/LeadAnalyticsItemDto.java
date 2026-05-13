package com.sao_rafael.crm_core.adapter.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record LeadAnalyticsItemDto(
        Long pacienteId,
        String nome,
        String telefone,
        String email,
        String canal,
        String origem,
        String responsavel,
        String etapa,
        String status,
        BigDecimal probabilidade,
        Integer score,
        BigDecimal valorPotencial,
        BigDecimal valorPonderado,
        String proximaAcao,
        LocalDateTime proximaAcaoDataHora,
        Boolean slaAtrasado,
        Long diasSemInteracao,
        Integer mensagens,
        String cidade,
        List<String> tags,
        List<LeadTimelineEventDto> timeline,
        List<LeadAlertDto> alertas
) {
}
