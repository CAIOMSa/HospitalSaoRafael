package com.sao_rafael.crm_core.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensagem")
public class MensagemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "conversa_id", nullable = false)
    private ConversaEntity conversa;

    @ManyToOne
    @JoinColumn(name = "enviado_por", nullable = false)
    private UsuarioEntity enviadoPor;

    @Column(name = "conteudo", length = 1000, nullable = false)
    private String conteudo;

    @Column(name = "data_envio", nullable = false)
    private LocalDateTime dataEnvio;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public ConversaEntity getConversa() {return conversa;}

    public void setConversa(ConversaEntity conversa) {this.conversa = conversa;}

    public UsuarioEntity getEnviadoPor() {return enviadoPor;}

    public void setEnviadoPor(UsuarioEntity enviadoPor) {this.enviadoPor = enviadoPor;}

    public String getConteudo() {return conteudo;}

    public void setConteudo(String conteudo) {this.conteudo = conteudo;}

    public LocalDateTime getDataEnvio() {return dataEnvio;}

    public void setDataEnvio(LocalDateTime dataEnvio) {this.dataEnvio = dataEnvio;}
}
