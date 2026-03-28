package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.ConsentimentoComunicacaoEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.ConsentimentoComunicacaoJpaRepository;
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
class ConsentimentoServiceTest {

    @Mock
    private ConsentimentoComunicacaoJpaRepository repository;

    @InjectMocks
    private ConsentimentoService service;

    @Test
    void getByIdShouldThrowWhenNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.getById(1L));

        assertEquals("Consentimento not found: 1", ex.getMessage());
    }

    @Test
    void updateShouldThrowWhenCanalIsMissing() {
        ConsentimentoComunicacaoEntity current = new ConsentimentoComunicacaoEntity();
        ConsentimentoComunicacaoEntity payload = new ConsentimentoComunicacaoEntity();

        when(repository.findById(1L)).thenReturn(Optional.of(current));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.update(1L, payload));

        assertEquals("O cliente deverá ser contatado por um canal válido.", ex.getMessage());
    }
}
