package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.MensagemEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.MensagemJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MensagemServiceTest {

    @Mock
    private MensagemJpaRepository repository;

    @InjectMocks
    private MensagemService service;

    @Test
    void updateShouldCopyConteudo() {
        MensagemEntity current = new MensagemEntity();
        MensagemEntity payload = new MensagemEntity();
        payload.setConteudo("Nova mensagem");

        when(repository.findById(1L)).thenReturn(Optional.of(current));
        when(repository.save(current)).thenReturn(current);

        MensagemEntity result = service.update(1L, payload);

        assertEquals("Nova mensagem", result.getConteudo());
    }
}
