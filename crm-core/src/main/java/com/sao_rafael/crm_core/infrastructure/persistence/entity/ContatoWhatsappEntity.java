package com.sao_rafael.crm_core.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "contato_whatsapp")
public class ContatoWhatsappEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private PacienteEntity paciente;

    @Column(name = "numero", length = 20, nullable = false)
    private String numero;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public PacienteEntity getPaciente() {return paciente;}

    public void setPaciente(PacienteEntity paciente) {this.paciente = paciente;}

    public String getNumero() {return numero;}

    public void setNumero(String numero) {this.numero = numero;}
}
