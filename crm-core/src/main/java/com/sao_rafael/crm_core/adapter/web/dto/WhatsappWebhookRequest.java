package com.sao_rafael.crm_core.adapter.web.dto;

public record WhatsappWebhookRequest(
        String numeroContato,
        Long pacienteId,
        String conteudo,
        Long statusId
) {
}
