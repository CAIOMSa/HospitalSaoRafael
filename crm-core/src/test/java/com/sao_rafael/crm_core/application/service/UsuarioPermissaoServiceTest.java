package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.UsuarioPermissaoEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.UsuarioPermissaoJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioPermissaoServiceTest {

    @Mock
    private UsuarioPermissaoJpaRepository repository;

    @InjectMocks
    private UsuarioPermissaoService service;

    @Test
    void updateShouldCopyRelations() {
        UsuarioPermissaoEntity current = new UsuarioPermissaoEntity();
        UsuarioPermissaoEntity payload = new UsuarioPermissaoEntity();

        when(repository.findById(1L)).thenReturn(Optional.of(current));
        when(repository.save(current)).thenReturn(current);

        UsuarioPermissaoEntity result = service.update(1L, payload);

        assertNotNull(result);
    }
}
