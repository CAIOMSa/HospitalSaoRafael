package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.ProcedimentoEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.ProcedimentoJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcedimentoServiceTest {

    @Mock
    private ProcedimentoJpaRepository repository;

    @InjectMocks
    private ProcedimentoService service;

    @Test
    void updateShouldCopyFields() {
        ProcedimentoEntity current = new ProcedimentoEntity();
        ProcedimentoEntity payload = new ProcedimentoEntity();
        payload.setNomeProcedimento("Raio-x");
        payload.setStatus(ProcedimentoEntity.StatusProcedimento.ATIVO);

        when(repository.findById(1L)).thenReturn(Optional.of(current));
        when(repository.save(current)).thenReturn(current);

        ProcedimentoEntity result = service.update(1L, payload);

        assertEquals("Raio-x", result.getNomeProcedimento());
        assertEquals(ProcedimentoEntity.StatusProcedimento.ATIVO, result.getStatus());
    }
}
