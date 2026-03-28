package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.EtapaEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.EtapaJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EtapaServiceTest {

    @Mock
    private EtapaJpaRepository repository;

    @InjectMocks
    private EtapaService service;

    @Test
    void updateShouldCopyNome() {
        EtapaEntity current = new EtapaEntity();
        current.setNome("Antiga");

        EtapaEntity payload = new EtapaEntity();
        payload.setNome("Nova");

        when(repository.findById(1L)).thenReturn(Optional.of(current));
        when(repository.save(current)).thenReturn(current);

        EtapaEntity result = service.update(1L, payload);

        assertEquals("Nova", result.getNome());
    }
}
