package com.sao_rafael.crm_core.adapter.web.dto.response;

import java.util.List;
import java.util.Map;

/**
 * DTO para configuração do Dashboard Dinâmico
 */
public class DynamicDashboardConfigDto {
    private List<DashboardWidgetDto> widgets;
    private String lastUpdated;
    private Long refreshInterval;

    public DynamicDashboardConfigDto(List<DashboardWidgetDto> widgets) {
        this.widgets = widgets;
        this.lastUpdated = java.time.Instant.now().toString();
        this.refreshInterval = 300000L; // 5 min padrão
    }

    // Getters e Setters
    public List<DashboardWidgetDto> getWidgets() {
        return widgets;
    }

    public void setWidgets(List<DashboardWidgetDto> widgets) {
        this.widgets = widgets;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Long getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(Long refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    /**
     * DTO para um Widget individual
     */
    public static class DashboardWidgetDto {
        private String id;
        private String type; // metric, chart, table, card
        private String title;
        private String subtitle;
        private Map<String, Object> data;

        public DashboardWidgetDto() {
        }

        public DashboardWidgetDto(String id, String type, String title, Map<String, Object> data) {
            this.id = id;
            this.type = type;
            this.title = title;
            this.data = data;
        }

        // Getters e Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public void setData(Map<String, Object> data) {
            this.data = data;
        }
    }
}
