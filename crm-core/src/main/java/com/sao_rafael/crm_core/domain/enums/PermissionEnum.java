package com.sao_rafael.crm_core.domain.enums;

/**
 * Enum de Permissões do Sistema
 * Define as permissões de acesso a funcionalidades específicas
 */
public enum PermissionEnum {
    READ_FINANCIAL("Visualizar Financeiro", "financeiro"),
    READ_REPORTS("Visualizar Relatórios", "relatorio"),
    MANAGE_SERVICES("Gerenciar Serviços", "servicos");

    private final String description;
    private final String screenCode;

    PermissionEnum(String description, String screenCode) {
        this.description = description;
        this.screenCode = screenCode;
    }

    public String getDescription() {
        return description;
    }

    public String getScreenCode() {
        return screenCode;
    }

    /**
     * Retorna a role do Keycloak correspondente
     * Padrão: ROLE_<screenCode uppercase>
     */
    public String getKeycloakRole() {
        return "ROLE_" + this.screenCode.toUpperCase();
    }
}
