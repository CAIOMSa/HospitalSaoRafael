package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.FuncionarioEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.UsuarioEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.FuncionarioJpaRepository;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.UsuarioJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioJpaRepository usuarioRepository;

    @Mock
    private FuncionarioJpaRepository funcionarioRepository;

    private UsuarioService service;

    @BeforeEach
    void setUp() {
        service = new UsuarioService(
            usuarioRepository,
            funcionarioRepository,
            "http://localhost:8080",
            "crm-realm",
            "crm-client"
        );
    }

    @Test
    void createShouldSetDefaultsAndSave() {
        FuncionarioEntity funcionario = new FuncionarioEntity();
        funcionario.setId(5L);

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setFuncionario(funcionario);
        usuario.setEmail("teste@empresa.com");
        usuario.setSenhaHash("hash");

        when(usuarioRepository.existsByEmail("teste@empresa.com")).thenReturn(false);
        when(funcionarioRepository.existsById(5L)).thenReturn(true);
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioEntity result = service.create(usuario);

        assertTrue(result.getAtivo());
        assertNotNull(result.getDataCriacao());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void updateShouldThrowWhenEmailAlreadyExists() {
        FuncionarioEntity funcionario = new FuncionarioEntity();
        funcionario.setId(10L);

        UsuarioEntity current = new UsuarioEntity();
        current.setId(1L);
        current.setEmail("old@empresa.com");

        UsuarioEntity payload = new UsuarioEntity();
        payload.setFuncionario(funcionario);
        payload.setEmail("novo@empresa.com");
        payload.setSenhaHash("novo");
        payload.setAtivo(true);
        payload.setUltimoLogin(LocalDateTime.now());

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(current));
        when(funcionarioRepository.existsById(10L)).thenReturn(true);
        when(usuarioRepository.existsByEmail("novo@empresa.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.update(1L, payload));

        assertEquals("Email already exists: novo@empresa.com", ex.getMessage());
    }

    @Test
    void buildLoginUrlShouldReturnKeycloakAuthorizationUrl() {
        String redirect = "http://localhost:3000/callback";

        String url = service.buildLoginUrl(redirect);

        assertTrue(url.contains("/realms/crm-realm/protocol/openid-connect/auth"));
        assertTrue(url.contains("client_id=crm-client"));
        assertTrue(url.contains("response_type=code"));
        assertTrue(url.contains("scope=openid"));
        assertTrue(url.contains("redirect_uri=http%3A%2F%2Flocalhost%3A3000%2Fcallback"));
    }
}
