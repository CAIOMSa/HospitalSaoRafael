package com.sao_rafael.crm_core.infrastructure.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

@Entity
@Table(name = "pesquisa_nps", uniqueConstraints = {@UniqueConstraint(columnNames = {"atendimento_id", "paciente_id"})})
public class PesquisaNPSEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "atendimento_id", nullable = false)
    private AtendimentoEntity atendimento;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private PacienteEntity paciente;

    @Column(name = "nota", nullable = false)
    @Min(0)
    @Max(10)
    private Integer nota;

    @Column(name = "data_resposta", nullable = false)
    private LocalDateTime dataResposta;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AtendimentoEntity getAtendimento() { return atendimento; }
    public void setAtendimento(AtendimentoEntity atendimento) { this.atendimento = atendimento; }

    public PacienteEntity getPaciente() { return paciente; }
    public void setPaciente(PacienteEntity paciente) { this.paciente = paciente; }

    public Integer getNota() { return nota; }
    public void setNota(Integer nota) { this.nota = nota; }

    public LocalDateTime getDataResposta() { return dataResposta; }
    public void setDataResposta(LocalDateTime dataResposta) { this.dataResposta = dataResposta; }

}
