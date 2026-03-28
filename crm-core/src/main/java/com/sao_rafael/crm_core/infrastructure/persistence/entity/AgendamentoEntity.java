package com.sao_rafael.crm_core.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "agendamento")
public class AgendamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private PacienteEntity paciente;

    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    private MedicoEntity medico;

    @ManyToOne
    @JoinColumn(name = "etapa_id", nullable = false)
    private EtapaEntity etapa;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private StatusEntity status;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime data_hora;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @PrePersist
    public void prePersist() {
        criadoEm = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        atualizadoEm = LocalDateTime.now();
    }

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public LocalDateTime getData_hora() {return data_hora;}

    public void setData_hora(LocalDateTime data_hora) {this.data_hora = data_hora;}

    public LocalDateTime getCriado_em() {return criadoEm;}

    public void setCriado_em(LocalDateTime criado_em) {this.criadoEm = criado_em;}

    public LocalDateTime getAtualizado_em() {return atualizadoEm;}

    public void setAtualizado_em(LocalDateTime atualizado_em) {this.atualizadoEm = atualizado_em;}

    public PacienteEntity getPaciente() {return paciente;}

    public void setPaciente(PacienteEntity paciente) {this.paciente = paciente;}

    public MedicoEntity getMedico() {return medico;}

    public void setMedico(MedicoEntity medico) {this.medico = medico;}

    public EtapaEntity getEtapa() {return etapa;}

    public void setEtapa(EtapaEntity etapa) {this.etapa = etapa;}

    public StatusEntity getStatus() {return status;}

    public void setStatus(StatusEntity status) {this.status = status;}

    public LocalDateTime getCriadoEm() {return criadoEm;}

    public void setCriadoEm(LocalDateTime criadoEm) {this.criadoEm = criadoEm;}

    public LocalDateTime getAtualizadoEm() {return atualizadoEm;}

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {this.atualizadoEm = atualizadoEm;}
}
