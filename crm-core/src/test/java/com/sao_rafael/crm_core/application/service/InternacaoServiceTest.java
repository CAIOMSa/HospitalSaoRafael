package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.InternacaoEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.InternacaoJpaRepository;
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
class InternacaoServiceTest {

    @Mock
    private InternacaoJpaRepository repository;

    @InjectMocks
    private InternacaoService service;

    @Test
    void updateShouldThrowWhenPacienteIsMissing() {
        InternacaoEntity current = new InternacaoEntity();
        InternacaoEntity payload = new InternacaoEntity();

        when(repository.findById(1L)).thenReturn(Optional.of(current));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.update(1L, payload));

        assertEquals("O paciente é obrigatório para acontecer uma internação.", ex.getMessage());
    }
}
