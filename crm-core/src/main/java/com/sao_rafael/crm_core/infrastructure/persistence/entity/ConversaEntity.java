package com.sao_rafael.crm_core.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversa")
public class ConversaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contato_id", nullable = false)
    private ContatoWhatsappEntity contato;

    @ManyToOne
    @JoinColumn(name = "conversa_id")
    private ConversaEntity conversa;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private StatusEntity status;

    @Column(name = "iniciado_em", nullable = false)
    private LocalDateTime iniciadoEm;

    @Column(name = "finalizado_em")
    private LocalDateTime finalizadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public ContatoWhatsappEntity getContato() {return contato;}

    public void setContato(ContatoWhatsappEntity contato) {this.contato = contato;}

    public ConversaEntity getConversa() {return conversa;}

    public void setConversa(ConversaEntity conversa) {this.conversa = conversa;}

    public StatusEntity getStatus() {return status;}

    public void setStatus(StatusEntity status) {this.status = status;}

    public LocalDateTime getIniciadoEm() {return iniciadoEm;}

    public void setIniciadoEm(LocalDateTime iniciadoEm) {this.iniciadoEm = iniciadoEm;}

    public LocalDateTime getFinalizadoEm() {return finalizadoEm;}

    public void setFinalizadoEm(LocalDateTime finalizadoEm) {this.finalizadoEm = finalizadoEm;}

    public LocalDateTime getAtualizadoEm() {return atualizadoEm;}

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {this.atualizadoEm = atualizadoEm;}
}
