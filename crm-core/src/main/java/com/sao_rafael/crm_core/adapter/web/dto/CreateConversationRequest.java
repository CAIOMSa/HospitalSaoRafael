package com.sao_rafael.crm_core.adapter.web.dto;

public record CreateConversationRequest(
        Long pacienteId,
        String numeroContato,
        Long statusId
) {
}
