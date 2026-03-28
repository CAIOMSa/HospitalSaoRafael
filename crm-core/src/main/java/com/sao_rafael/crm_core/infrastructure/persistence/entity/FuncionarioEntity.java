package com.sao_rafael.crm_core.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "funcionarios")
@Inheritance(strategy = InheritanceType.JOINED)
public class FuncionarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "funcionario")
    private UsuarioEntity usuario;

    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @Column(name = "cpf", length = 14, nullable = false, unique = true)
    private String cpf;

    @ManyToOne
    @JoinColumn(name = "cargo_id", nullable = false)
    private CargoEntity cargo;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    public void prePersist() {
        criadoEm = LocalDateTime.now();
    }

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getNome() {return nome;}

    public void setNome(String nome) {this.nome = nome;}

    public String getCpf() {return cpf;}

    public void setCpf(String cpf) {this.cpf = cpf;}

    public CargoEntity getCargo() {return cargo;}

    public void setCargo(CargoEntity cargo) {this.cargo = cargo;}

    public UsuarioEntity getUsuario() {return usuario;}

    public void setUsuario(UsuarioEntity usuario) {this.usuario = usuario;}

    public Boolean getAtivo() {return ativo;}

    public void setAtivo(Boolean ativo) {this.ativo = ativo;}

    public LocalDateTime getCriadoEm() {return criadoEm;}

    public void setCriadoEm(LocalDateTime criadoEm) {this.criadoEm = criadoEm;}
}
