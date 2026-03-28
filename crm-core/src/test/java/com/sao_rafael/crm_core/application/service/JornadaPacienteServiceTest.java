package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.JornadaPacienteEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.JornadaPacienteJpaRepository;
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
class JornadaPacienteServiceTest {

    @Mock
    private JornadaPacienteJpaRepository repository;

    @InjectMocks
    private JornadaPacienteService service;

    @Test
    void updateShouldThrowWhenPacienteIsMissing() {
        JornadaPacienteEntity current = new JornadaPacienteEntity();
        JornadaPacienteEntity payload = new JornadaPacienteEntity();

        when(repository.findById(1L)).thenReturn(Optional.of(current));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.update(1L, payload));

        assertEquals("O Paciente é obrigatório para existir sua jornada", ex.getMessage());
    }
}
