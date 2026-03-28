package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.PacienteEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.PacienteJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock
    private PacienteJpaRepository repository;

    @InjectMocks
    private PacienteService service;

    @Test
    void updateShouldCopyFields() {
        PacienteEntity current = new PacienteEntity();
        PacienteEntity payload = new PacienteEntity();
        payload.setNome("Ana");
        payload.setCpf("123");
        payload.setTelefone("9999-0000");
        payload.setEmail("ana@teste.com");

        when(repository.findById(1L)).thenReturn(Optional.of(current));
        when(repository.save(current)).thenReturn(current);

        PacienteEntity result = service.update(1L, payload);

        assertEquals("Ana", result.getNome());
        assertEquals("123", result.getCpf());
        assertEquals("9999-0000", result.getTelefone());
        assertEquals("ana@teste.com", result.getEmail());
    }
}
