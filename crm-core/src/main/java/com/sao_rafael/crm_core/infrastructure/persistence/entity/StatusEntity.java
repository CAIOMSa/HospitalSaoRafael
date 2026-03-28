package com.sao_rafael.crm_core.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "status")
public class StatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria_status", nullable = false)
    private CategoriaStatus categoria;

    public enum CategoriaStatus {
        INTERNACAO,
        ATENDIMENTO,
        PROCEDIMENTO,
        CONSULTA
    }

    @Column(name = "nome", length = 30, nullable = false)
    private String nome;

    @Column(name = "descricao", length = 150, nullable = false)
    private String descricao;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getNome() {return nome;}

    public void setNome(String nome) {this.nome = nome;}

    public String getDescricao() {return descricao;}

    public void setDescricao(String descricao) {this.descricao = descricao;}

    public CategoriaStatus getCategoria() {return categoria;}

    public void setCategoria(CategoriaStatus categoria) {this.categoria = categoria;}
}
