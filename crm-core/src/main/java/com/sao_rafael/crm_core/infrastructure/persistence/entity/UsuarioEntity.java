package com.sao_rafael.crm_core.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "funcionario_id", nullable = false, unique = true)
    private FuncionarioEntity funcionario;

    @Column(name = "email", length = 150, nullable = false, unique = true)
    private String email;

    @Column(name = "senha_hash", length = 255, nullable = false)
    private String senhaHash;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    public void prePersist() {
        dataCriacao = LocalDateTime.now();
    }

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public FuncionarioEntity getFuncionario() {return funcionario;}

    public void setFuncionario(FuncionarioEntity funcionario) {this.funcionario = funcionario;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getSenhaHash() {return senhaHash;}

    public void setSenhaHash(String senhaHash) {this.senhaHash = senhaHash;}

    public Boolean getAtivo() {return ativo;}

    public void setAtivo(Boolean ativo) {this.ativo = ativo;}

    public LocalDateTime getDataCriacao() {return dataCriacao;}

    public void setDataCriacao(LocalDateTime dataCriacao) {this.dataCriacao = dataCriacao;}

    public LocalDateTime getUltimoLogin() {return ultimoLogin;}

    public void setUltimoLogin(LocalDateTime ultimoLogin) {this.ultimoLogin = ultimoLogin;}
}
