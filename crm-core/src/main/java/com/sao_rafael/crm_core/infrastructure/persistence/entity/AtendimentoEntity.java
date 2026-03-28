package com.sao_rafael.crm_core.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "atendimentos")
public class AtendimentoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private PacienteEntity paciente;

    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    private MedicoEntity medico;

    @OneToOne
    @JoinColumn(name = "agendamento_id", nullable = false)
    private AgendamentoEntity agendamento;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    @ManyToOne
    @JoinColumn(name = "etapa_id", nullable = false)
    private EtapaEntity etapa;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @PrePersist
    public void prePersist(){
        criadoEm = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        atualizadoEm = LocalDateTime.now();
    }

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public MedicoEntity getMedico() {return medico;}

    public void setMedico(MedicoEntity medico) {this.medico = medico;}

    public LocalDateTime getDataInicio() {return dataInicio;}

    public void setDataInicio(LocalDateTime dataInicio) {this.dataInicio = dataInicio;}

    public LocalDateTime getDataFim() {return dataFim;}

    public void setDataFim(LocalDateTime dataFim) {this.dataFim = dataFim;}

    public String getObservacoes() {return observacoes;}

    public void setObservacoes(String observacoes) {this.observacoes = observacoes;}

    public LocalDateTime getCriadoEm() {return criadoEm;}

    public void setCriadoEm(LocalDateTime criadoEm) {this.criadoEm = criadoEm;}

    public LocalDateTime getAtualizadoEm() {return atualizadoEm;}

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {this.atualizadoEm = atualizadoEm;}

    public PacienteEntity getPaciente() {return paciente;}

    public void setPaciente(PacienteEntity paciente) {this.paciente = paciente;}

    public AgendamentoEntity getAgendamento() {return agendamento;}

    public void setAgendamento(AgendamentoEntity agendamento) {this.agendamento = agendamento;}

    public EtapaEntity getEtapa() {return etapa;}

    public void setEtapa(EtapaEntity etapa) {this.etapa = etapa;}
}
