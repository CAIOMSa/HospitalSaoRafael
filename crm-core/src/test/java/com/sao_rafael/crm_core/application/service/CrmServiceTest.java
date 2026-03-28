package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.CrmEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.MedicoEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.CrmJpaRepository;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.MedicoJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrmServiceTest {

    @Mock
    private CrmJpaRepository repository;

    @Mock
    private MedicoJpaRepository medicoRepository;

    @InjectMocks
    private CrmService service;

    @Test
    void createShouldThrowWhenFuncionarioNotFound() {
        MedicoEntity medico = new MedicoEntity();
        medico.setId(1L);

        CrmEntity crm = new CrmEntity();
        crm.setCrm("12345");
        crm.setUf(CrmEntity.Uf.SP);
        crm.setMedico(medico);

        when(medicoRepository.existsById(7L)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.create(crm));

        assertEquals("Funcionario not found: 7", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void createShouldSaveWhenFuncionarioExists() {
        MedicoEntity medico = new MedicoEntity();
        medico.setId(3L);

        CrmEntity crm = new CrmEntity();
        crm.setCrm("321");
        crm.setUf(CrmEntity.Uf.SP);
        crm.setMedico(medico);

        when(medicoRepository.existsById(3L)).thenReturn(true);
        when(repository.save(any(CrmEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CrmEntity result = service.create(crm);

        assertEquals("CRM-321", result.getCrm());
        verify(repository).save(crm);
    }

    @Test
    void updateShouldCopyFields() {
        MedicoEntity medicoAtual = new MedicoEntity();
        medicoAtual.setId(1L);

        MedicoEntity medicoNovo = new MedicoEntity();
        medicoNovo.setId(2L);

        CrmEntity current = new CrmEntity();
        current.setId(1L);
        current.setCrm("OLD");
        current.setUf(CrmEntity.Uf.RJ);
        current.setMedico(medicoAtual);

        CrmEntity payload = new CrmEntity();
        payload.setCrm("NEW");
        payload.setUf(CrmEntity.Uf.SP);
        payload.setMedico(medicoNovo);

        when(repository.findById(1L)).thenReturn(Optional.of(current));
        when(medicoRepository.existsById(2L)).thenReturn(true);
        when(repository.save(any(CrmEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CrmEntity result = service.update(1L, payload);

        assertEquals("NEW", result.getCrm());
        assertEquals(CrmEntity.Uf.SP, result.getUf());
        assertEquals(2L, result.getMedico().getId());
    }
}
