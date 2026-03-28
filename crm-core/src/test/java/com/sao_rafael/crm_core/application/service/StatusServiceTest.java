package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.StatusEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.StatusJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatusServiceTest {

    @Mock
    private StatusJpaRepository repository;

    @InjectMocks
    private StatusService service;

    @Test
    void updateShouldCopyFields() {
        StatusEntity current = new StatusEntity();
        StatusEntity payload = new StatusEntity();
        payload.setCategoria(StatusEntity.CategoriaStatus.ATENDIMENTO);
        payload.setNome("Finalizado");
        payload.setDescricao("Fluxo encerrado");

        when(repository.findById(1L)).thenReturn(Optional.of(current));
        when(repository.save(current)).thenReturn(current);

        StatusEntity result = service.update(1L, payload);

        assertEquals(StatusEntity.CategoriaStatus.ATENDIMENTO, result.getCategoria());
        assertEquals("Finalizado", result.getNome());
        assertEquals("Fluxo encerrado", result.getDescricao());
    }
}
