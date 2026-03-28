package com.sao_rafael.crm_core.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "procedimentos")
public class ProcedimentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_procedimento", length = 150,nullable = false, unique = true)
    private String nomeProcedimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_procedimento", nullable = false)
    private StatusProcedimento status;

    public enum StatusProcedimento {
        ATIVO,
        INATIVO,
        SUSPENSO
    }

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getNomeProcedimento() {return nomeProcedimento;}

    public void setNomeProcedimento(String nomeProcedimento) {this.nomeProcedimento = nomeProcedimento;}

    public StatusProcedimento getStatus() {return status;}

    public void setStatus(StatusProcedimento status) {this.status = status;}
}
