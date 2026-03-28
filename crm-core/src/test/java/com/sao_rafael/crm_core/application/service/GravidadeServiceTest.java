package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.GravidadeEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.GravidadeJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GravidadeServiceTest {

    @Mock
    private GravidadeJpaRepository repository;

    @InjectMocks
    private GravidadeService service;

    @Test
    void updateShouldCopyFields() {
        GravidadeEntity current = new GravidadeEntity();
        GravidadeEntity payload = new GravidadeEntity();
        payload.setNome("Alta");
        payload.setDescricao("Risco elevado");

        when(repository.findById(1L)).thenReturn(Optional.of(current));
        when(repository.save(current)).thenReturn(current);

        GravidadeEntity result = service.update(1L, payload);

        assertEquals("Alta", result.getNome());
        assertEquals("Risco elevado", result.getDescricao());
    }
}
