package com.sao_rafael.crm_core.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario_permissao", uniqueConstraints = {@UniqueConstraint(columnNames = {"usuario_id", "permissao_id"})})
public class UsuarioPermissaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    @ManyToOne
    @JoinColumn(name = "permissao_id", nullable = false)
    private PermissaoEntity permissao;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UsuarioEntity getUsuario() { return usuario; }
    public void setUsuario(UsuarioEntity usuario) { this.usuario = usuario; }

    public PermissaoEntity getPermissao() { return permissao; }
    public void setPermissao(PermissaoEntity permissao) { this.permissao = permissao; }
}
