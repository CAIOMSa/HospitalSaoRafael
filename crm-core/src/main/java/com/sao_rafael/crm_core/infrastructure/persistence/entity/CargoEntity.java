package com.sao_rafael.crm_core.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cargos")
public class CargoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cargo", length = 30, nullable = false)
    private String cargo;

    @Column(name = "departamento", length = 100, nullable = false)
    private String departamento;

    @Column(name = "nivel_hierarquico", length = 30, nullable = false)
    private String nivelHierarquico;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime inicioVinculo;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    public void prePersist() {
        inicioVinculo = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        atualizadoEm = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getDepartamento() {return departamento;}

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getNivelHierarquico() {
        return nivelHierarquico;
    }

    public void setNivelHierarquico(String nivelHierarquico) {
        this.nivelHierarquico = nivelHierarquico;
    }

    public LocalDateTime getDataInicio() {
        return inicioVinculo;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.inicioVinculo = dataInicio;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public LocalDateTime getInicioVinculo() {return inicioVinculo;}

    public void setInicioVinculo(LocalDateTime inicioVinculo) {this.inicioVinculo = inicioVinculo;}
}
