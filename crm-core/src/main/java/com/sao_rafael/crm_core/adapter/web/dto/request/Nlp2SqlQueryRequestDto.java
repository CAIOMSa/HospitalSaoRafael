package com.sao_rafael.crm_core.adapter.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Nlp2SqlQueryRequestDto {

    @NotBlank(message = "A pergunta é obrigatória")
    @Size(max = 280, message = "A pergunta deve ter no máximo 280 caracteres")
    private String question;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
