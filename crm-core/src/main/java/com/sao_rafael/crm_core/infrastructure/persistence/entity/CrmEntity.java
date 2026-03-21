package com.sao_rafael.crm_core.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "crms")
public class CrmEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "crm", length = 100, nullable = false)
    private String crm;

    @Column(name = "id_funcionario", nullable = false)
    private Long idFuncionario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public Long getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(Long idFuncionario) {
        this.idFuncionario = idFuncionario;
    }
}
