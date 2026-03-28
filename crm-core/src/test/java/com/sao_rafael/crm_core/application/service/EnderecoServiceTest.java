package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.EnderecoEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.EnderecoJpaRepository;
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
class EnderecoServiceTest {

    @Mock
    private EnderecoJpaRepository repository;

    @InjectMocks
    private EnderecoService service;

    @Test
    void getByIdShouldThrowWhenNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.getById(1L));

        assertEquals("Endereço not found: 1", ex.getMessage());
    }

    @Test
    void updateShouldThrowWhenRuaIsMissing() {
        EnderecoEntity current = new EnderecoEntity();
        EnderecoEntity payload = new EnderecoEntity();

        when(repository.findById(1L)).thenReturn(Optional.of(current));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.update(1L, payload));

        assertEquals("O cliente deverá ter um nome de rua valido", ex.getMessage());
    }
}
