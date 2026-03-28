package com.sao_rafael.crm_core.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cirurgias")
public class CirurgiaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "procedimento_id", nullable = false)
    private ProcedimentoEntity procedimento;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private PacienteEntity paciente;

    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    private MedicoEntity medico;

    @ManyToOne
    @JoinColumn(name = "gravidade_id")
    private GravidadeEntity gravidade;

    @Column(name = "data_agendada", nullable = false)
    private LocalDateTime dataAgendada;

    @Column(name = "data_realizada")
    private LocalDateTime dataRealizada;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private StatusEntity status;

    @Column(name = "sala", length = 30, nullable = false)
    private String sala;

    @Column(name = "risco", nullable = false)
    @Enumerated(EnumType.STRING)
    private RiscoCirurgia risco;

    public enum RiscoCirurgia {

        BAIXO("Baixo risco"),
        MEDIO("Médio risco"),
        ALTO("Alto risco"),
        CRITICO("Risco crítico");

        private final String descricao;

        RiscoCirurgia(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

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

    public ProcedimentoEntity getProcedimento() {return procedimento;}

    public void setProcedimento(ProcedimentoEntity procedimento) {this.procedimento = procedimento;}

    public PacienteEntity getPaciente() {return paciente;}

    public void setPaciente(PacienteEntity paciente) {this.paciente = paciente;}

    public MedicoEntity getMedico() {return medico;}

    public void setMedico(MedicoEntity medico) {this.medico = medico;}

    public GravidadeEntity getGravidade() {return gravidade;}

    public void setGravidade(GravidadeEntity gravidade) {this.gravidade = gravidade;}

    public RiscoCirurgia getRisco() {return risco;}

    public void setRisco(RiscoCirurgia risco) {this.risco = risco;}

    public LocalDateTime getAtualizadoEm() {return atualizadoEm;}

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {this.atualizadoEm = atualizadoEm;}

    public LocalDateTime getDataRealizada() {return dataRealizada;}

    public void setDataRealizada(LocalDateTime dataRealizada) {this.dataRealizada = dataRealizada;}

    public LocalDateTime getDataAgendada() {return dataAgendada;}

    public void setDataAgendada(LocalDateTime dataAgendada) {this.dataAgendada = dataAgendada;}

    public LocalDateTime getCriadoEm() {return criadoEm;}

    public void setCriadoEm(LocalDateTime criadoEm) {this.criadoEm = criadoEm;}

    public String getSala() {return sala;}

    public void setSala(String sala) {this.sala = sala;}

    public StatusEntity getStatus() {return status;}

    public void setStatus(StatusEntity status) {this.status = status;}
}
