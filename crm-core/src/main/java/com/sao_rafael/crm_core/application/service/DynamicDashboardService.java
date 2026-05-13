package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.adapter.web.dto.response.DynamicDashboardConfigDto;
import com.sao_rafael.crm_core.adapter.web.dto.response.DynamicDashboardConfigDto.DashboardWidgetDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Serviço para construir configurações do Dashboard Dinâmico
 * Valida dados e monta widgets de forma segura
 */
@Service
public class DynamicDashboardService {

    private final boolean nlp2sqlEnabled;

    public DynamicDashboardService(@Value("${features.nlp2sql.enabled:true}") boolean nlp2sqlEnabled) {
        this.nlp2sqlEnabled = nlp2sqlEnabled;
    }

    /**
     * Retorna configuração do dashboard orientado por NLP2SQL.
     */
    public DynamicDashboardConfigDto getDashboardConfig() {
        List<DashboardWidgetDto> widgets = new ArrayList<>();

        widgets.add(createNlp2SqlWidget(
            "nlp2sql-assistant",
            "Assistente NLP2SQL",
            createNlp2SqlData(
                nlp2sqlEnabled
                    ? "Pergunte em linguagem natural. A resposta será exibida em tabela e insights visuais automáticos (top 5 e gráficos)."
                    : "Módulo NLP2SQL desativado no backend. Ative NLP2SQL_ENABLED para consultar.",
                "Ex: pacientes de agosto",
                280,
                nlp2sqlEnabled
            )
        ));

        DynamicDashboardConfigDto config = new DynamicDashboardConfigDto(widgets);
        config.setRefreshInterval(0L);

        return config;
    }

    private DashboardWidgetDto createNlp2SqlWidget(String id, String title, Map<String, Object> data) {
        return new DashboardWidgetDto(id, "nlp2sql", title, data);
    }

    private Map<String, Object> createNlp2SqlData(
        String description,
        String inputPlaceholder,
        int maxQuestionLength,
        boolean enabled
    ) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("description", description);
        data.put("inputPlaceholder", inputPlaceholder);
        data.put("maxQuestionLength", maxQuestionLength);
        data.put("enabled", enabled);
        return data;
    }
}
