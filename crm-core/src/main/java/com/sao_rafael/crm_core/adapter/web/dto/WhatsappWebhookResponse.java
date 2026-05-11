package com.sao_rafael.crm_core.adapter.web.dto;

public record WhatsappWebhookResponse(
        String status,
        Long conversaId,
        Long mensagemId,
        String detalhe
) {
}
