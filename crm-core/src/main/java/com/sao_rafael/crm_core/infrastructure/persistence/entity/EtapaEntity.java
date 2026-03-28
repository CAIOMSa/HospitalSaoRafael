package com.sao_rafael.crm_core.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "etapa", uniqueConstraints = @UniqueConstraint(columnNames = "nome"))
public class EtapaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", length = 50, nullable = false)
    private String nome;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getNome() {return nome;}

    public void setNome(String nome) {this.nome = nome;}
}
