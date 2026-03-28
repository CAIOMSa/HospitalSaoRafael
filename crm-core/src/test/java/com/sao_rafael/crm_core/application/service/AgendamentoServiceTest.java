package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.repository.AgendamentoJpaRepository;
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
class AgendamentoServiceTest {

    @Mock
    private AgendamentoJpaRepository repository;

    @InjectMocks
    private AgendamentoService service;

    @Test
    void getByIdShouldThrowWhenNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.getById(1L));

        assertEquals("Agendamento not found: 1", ex.getMessage());
    }
}
