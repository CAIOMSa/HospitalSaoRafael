package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.CrmEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.MedicoEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.MedicoJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicoServiceTest {

    @Mock
    private MedicoJpaRepository repository;

    @InjectMocks
    private MedicoService service;

    @Test
    void updateShouldThrowWhenCrmIsMissing() {
        MedicoEntity current = new MedicoEntity();
        MedicoEntity payload = new MedicoEntity();

        when(repository.findById(1L)).thenReturn(Optional.of(current));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.update(1L, payload));

        assertEquals("Todo médico deverá ter um CRM", ex.getMessage());
    }

    @Test
    void updateShouldCopyCrm() {
        MedicoEntity current = new MedicoEntity();
        MedicoEntity payload = new MedicoEntity();
        CrmEntity crm = new CrmEntity();
        payload.setCrm(crm);

        when(repository.findById(2L)).thenReturn(Optional.of(current));
        when(repository.save(current)).thenReturn(current);

        MedicoEntity result = service.update(2L, payload);

        assertEquals(crm, result.getCrm());
    }
}
