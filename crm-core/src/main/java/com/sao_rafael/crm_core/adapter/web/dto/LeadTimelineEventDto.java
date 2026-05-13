package com.sao_rafael.crm_core.adapter.web.dto;

import java.time.LocalDateTime;

public record LeadTimelineEventDto(
        LocalDateTime dataHora,
        String tipo,
        String descricao,
        String detalhe
) {
}
