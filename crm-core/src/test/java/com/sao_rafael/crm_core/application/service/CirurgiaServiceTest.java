package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.CirurgiaEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.CirurgiaJpaRepository;
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
class CirurgiaServiceTest {

    @Mock
    private CirurgiaJpaRepository repository;

    @InjectMocks
    private CirurgiaService service;

    @Test
    void getByIdShouldThrowWhenNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.getById(1L));

        assertEquals("Cirurgia not found: 1", ex.getMessage());
    }

    @Test
    void updateShouldThrowWhenDataAgendadaIsMissing() {
        CirurgiaEntity current = new CirurgiaEntity();
        CirurgiaEntity payload = new CirurgiaEntity();

        when(repository.findById(1L)).thenReturn(Optional.of(current));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.update(1L, payload));

        assertEquals("O Paciente é obrigatório para a operação", ex.getMessage());
    }
}
