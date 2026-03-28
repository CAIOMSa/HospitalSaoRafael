package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.UsuarioEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.FuncionarioJpaRepository;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.UsuarioJpaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class UsuarioService extends BaseCrudService<UsuarioEntity, Long> {

    private final UsuarioJpaRepository usuarioRepository;
    private final FuncionarioJpaRepository funcionarioRepository;
    private final String keycloakAuthServerUrl;
    private final String keycloakRealm;
    private final String keycloakClientId;

    public UsuarioService(UsuarioJpaRepository usuarioRepository,
                         FuncionarioJpaRepository funcionarioRepository,
                         @Value("${keycloak.auth-server-url}") String keycloakAuthServerUrl,
                         @Value("${keycloak.realm}") String keycloakRealm,
                         @Value("${keycloak.resource}") String keycloakClientId) {
        super(usuarioRepository);
        this.usuarioRepository = usuarioRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.keycloakAuthServerUrl = keycloakAuthServerUrl;
        this.keycloakRealm = keycloakRealm;
        this.keycloakClientId = keycloakClientId;
    }

    @Override
    protected void beforeCreate(UsuarioEntity usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + usuario.getEmail());
        }
        if (!funcionarioRepository.existsById(usuario.getFuncionario().getId())) {
            throw new IllegalArgumentException("Funcionario not found: " + usuario.getFuncionario());
        }
        if (usuario.getAtivo() == null) {
            usuario.setAtivo(true);
        }
        if (usuario.getDataCriacao() == null) {
            usuario.setDataCriacao(LocalDateTime.now());
        }
    }

    @Override
    protected void copyForUpdate(UsuarioEntity current, UsuarioEntity payload) {
        if (!funcionarioRepository.existsById(payload.getFuncionario().getId())) {
            throw new IllegalArgumentException("Funcionario not found: " + payload.getFuncionario());
        }
        if (!current.getEmail().equals(payload.getEmail()) && usuarioRepository.existsByEmail(payload.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + payload.getEmail());
        }
        current.setFuncionario(payload.getFuncionario());
        current.setEmail(payload.getEmail());
        current.setSenhaHash(payload.getSenhaHash());
        current.setAtivo(payload.getAtivo());
        current.setUltimoLogin(payload.getUltimoLogin());
    }

    @Override
    protected String entityName() {
        return "Usuario";
    }

    public String buildLoginUrl(String redirectUri) {
        String encodedRedirect = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
        return keycloakAuthServerUrl
            + "/realms/" + keycloakRealm
            + "/protocol/openid-connect/auth"
            + "?client_id=" + keycloakClientId
            + "&response_type=code"
            + "&scope=openid"
            + "&redirect_uri=" + encodedRedirect;
    }
}
