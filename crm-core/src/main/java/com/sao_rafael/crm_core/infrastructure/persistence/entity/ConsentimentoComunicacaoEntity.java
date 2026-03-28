package com.sao_rafael.crm_core.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consentimento_comunicacao")
public class ConsentimentoComunicacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "paciente_id", nullable = false)
    private Long paciente_id;

    @Column(name = "concedido", nullable = false)
    private boolean concedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "canal", nullable = false)
    private CanalComunicacao canal;

    public enum CanalComunicacao {
        EMAIL,
        SMS,
        WHATSAPP,
        TELEFONE
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "finalidade", nullable = false)
    private FinalidadeComunicacao finalidade;

    public enum FinalidadeComunicacao {
        MARKETING,
        LEMBRETE_CONSULTA,
        RESULTADO_EXAME,
        COBRANCA
    }

    @Column(name = "data_consentimento", nullable = false)
    private LocalDateTime dataConsentimento;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @Column(name = "origem_atendimento", length = 100, nullable = false)
    private String origemAtendimento;

    @ManyToOne
    @JoinColumn(name = "registrado_por", nullable = false)
    private UsuarioEntity registradoPor;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public Long getPaciente_id() {return paciente_id;}

    public void setPaciente_id(Long paciente_id) {this.paciente_id = paciente_id;}

    public boolean isConcedido() {return concedido;}

    public void setConcedido(boolean concedido) {this.concedido = concedido;}

    public LocalDateTime getDataConsentimento() {return dataConsentimento;}

    public void setDataConsentimento(LocalDateTime dataConsentimento) {this.dataConsentimento = dataConsentimento;}

    public LocalDateTime getAtualizadoEm() {return atualizadoEm;}

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {this.atualizadoEm = atualizadoEm;}

    public String getOrigemAtendimento() {return origemAtendimento;}

    public void setOrigemAtendimento(String origemAtendimento) {this.origemAtendimento = origemAtendimento;}

    public CanalComunicacao getCanal() {return canal;}

    public void setCanal(CanalComunicacao canal) {this.canal = canal;}

    public FinalidadeComunicacao getFinalidade() {return finalidade;}

    public void setFinalidade(FinalidadeComunicacao finalidade) {this.finalidade = finalidade;}

    public UsuarioEntity getRegistradoPor() {return registradoPor;}

    public void setRegistradoPor(UsuarioEntity registradoPor) {this.registradoPor = registradoPor;}
}
