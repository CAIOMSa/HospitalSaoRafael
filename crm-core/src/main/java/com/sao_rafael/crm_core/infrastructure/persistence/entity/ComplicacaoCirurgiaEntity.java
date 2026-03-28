package com.sao_rafael.crm_core.infrastructure.persistence.entity;

import jakarta.persistence.*;


@Entity
@Table(name = "complicacoes_cirurgia")
public class ComplicacaoCirurgiaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cirurgia_id", nullable = false)
    private CirurgiaEntity cirurgia;

    @Column(name = "descricao", length = 150, nullable = false)
    private String descricao;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public CirurgiaEntity getCirurgia() {return cirurgia;}

    public void setCirurgia(CirurgiaEntity cirurgia) {this.cirurgia = cirurgia;}

    public String getDescricao() {return descricao;}

    public void setDescricao(String descricao) {this.descricao = descricao;}
}
