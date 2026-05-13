package com.sao_rafael.crm_core.adapter.web.dto.response;

import java.util.List;
import java.util.Map;

public class Nlp2SqlQueryResponseDto {

    private boolean success;
    private String answer;
    private String generatedSql;
    private List<String> columns;
    private List<Map<String, Object>> rows;
    private int rowCount;
    private String source;
    private Map<String, Object> insightsPlan;
    private GuardrailDto guardrail;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getGeneratedSql() {
        return generatedSql;
    }

    public void setGeneratedSql(String generatedSql) {
        this.generatedSql = generatedSql;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<Map<String, Object>> getRows() {
        return rows;
    }

    public void setRows(List<Map<String, Object>> rows) {
        this.rows = rows;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Map<String, Object> getInsightsPlan() {
        return insightsPlan;
    }

    public void setInsightsPlan(Map<String, Object> insightsPlan) {
        this.insightsPlan = insightsPlan;
    }

    public GuardrailDto getGuardrail() {
        return guardrail;
    }

    public void setGuardrail(GuardrailDto guardrail) {
        this.guardrail = guardrail;
    }

    public static class GuardrailDto {
        private boolean blocked;
        private String reason;

        public GuardrailDto() {
        }

        public GuardrailDto(boolean blocked, String reason) {
            this.blocked = blocked;
            this.reason = reason;
        }

        public boolean isBlocked() {
            return blocked;
        }

        public void setBlocked(boolean blocked) {
            this.blocked = blocked;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
