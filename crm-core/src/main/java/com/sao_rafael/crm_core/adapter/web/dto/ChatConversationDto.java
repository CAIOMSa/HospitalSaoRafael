package com.sao_rafael.crm_core.adapter.web.dto;

import java.time.LocalDateTime;

public record ChatConversationDto(
        Long id,
        String contatoNumero,
        String pacienteNome,
        String ultimaMensagem,
        LocalDateTime ultimaMensagemEm
) {
}
