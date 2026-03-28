package com.sao_rafael.crm_core.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "jornada_paciente")
public class JornadaPacienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "etapa_id", nullable = false)
    private EtapaEntity etapa;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private PacienteEntity paciente;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public EtapaEntity getEtapa() {return etapa;}

    public void setEtapa(EtapaEntity etapa) {this.etapa = etapa;}

    public PacienteEntity getPaciente() {return paciente;}

    public void setPaciente(PacienteEntity paciente) {this.paciente = paciente;}

    public LocalDateTime getDataInicio() {return dataInicio;}

    public void setDataInicio(LocalDateTime dataInicio) {this.dataInicio = dataInicio;}

    public LocalDateTime getDataFim() {return dataFim;}

    public void setDataFim(LocalDateTime dataFim) {this.dataFim = dataFim;}
}
