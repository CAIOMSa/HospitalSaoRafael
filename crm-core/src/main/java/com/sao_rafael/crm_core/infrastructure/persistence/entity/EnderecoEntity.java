package com.sao_rafael.crm_core.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "endereco")
public class EnderecoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rua", length = 150, nullable = false)
    private String rua;

    @Column(name = "numero", length = 10, nullable = false)
    private String numero;

    @Column(name = "bairro", length = 100, nullable = false)
    private String bairro;

    @Column(name = "cidade", length = 100, nullable = false)
    private String cidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private Uf estado;

    public enum Uf{
        AC, AL, AP, AM, BA, CE, DF, ES, GO, MA, MT, MS, MG, PA, PB, PR, PE, PI, RJ, RN, RS, RO, RR, SC, SP, SE, TO
    }

    @Column(name = "cep", length = 9, nullable = false)
    private String cep;

    @Column(name = "complemento", length = 100)
    private String complemento;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getRua() {return rua;}

    public void setRua(String rua) {this.rua = rua;}

    public String getNumero() {return numero;}

    public void setNumero(String numero) {this.numero = numero;}

    public String getBairro() {return bairro;}

    public void setBairro(String bairro) {this.bairro = bairro;}

    public String getCidade() {return cidade;}

    public void setCidade(String cidade) {this.cidade = cidade;}

    public Uf getEstado() {return estado;}

    public void setEstado(Uf estado) {this.estado = estado;}

    public String getCep() {return cep;}

    public void setCep(String cep) {this.cep = cep;}

    public String getComplemento() {return complemento;}

    public void setComplemento(String complemento) {this.complemento = complemento;}
}
