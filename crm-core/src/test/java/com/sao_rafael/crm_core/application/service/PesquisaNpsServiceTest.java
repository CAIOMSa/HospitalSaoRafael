package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.PesquisaNPSEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.PesquisaNpsJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PesquisaNpsServiceTest {

    @Mock
    private PesquisaNpsJpaRepository repository;

    @InjectMocks
    private PesquisaNpsService service;

    @Test
    void createShouldThrowWhenNotaIsInvalid() {
        PesquisaNPSEntity entity = new PesquisaNPSEntity();
        entity.setNota(11);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.create(entity));

        assertEquals("Nota NPS deve ser entre 0 e 10", ex.getMessage());
    }

    @Test
    void classificarNpsShouldReturnExpectedCategories() {
        assertEquals("DETRATOR", service.classificarNPS(6));
        assertEquals("NEUTRO", service.classificarNPS(8));
        assertEquals("PROMOTOR", service.classificarNPS(10));
    }
}
