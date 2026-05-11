package com.sao_rafael.crm_core.adapter.web.dto;

import java.time.LocalDateTime;

public record ChatMessageDto(
        Long id,
        Long conversaId,
        String conteudo,
        LocalDateTime dataEnvio,
        String sentido,
        String autor
) {
}
