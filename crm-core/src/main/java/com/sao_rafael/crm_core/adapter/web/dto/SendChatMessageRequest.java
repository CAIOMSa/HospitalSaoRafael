package com.sao_rafael.crm_core.adapter.web.dto;

public record SendChatMessageRequest(
        Long conversaId,
        String conteudo,
        Long usuarioId,
        String usuarioEmail
) {
}
