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
    void createShouldThrowWhenMedicoNotFound() {
        MedicoEntity medico = new MedicoEntity();
        medico.setId(1L);

        CrmEntity crm = new CrmEntity();
        crm.setCrm("12345");
        crm.setUf(CrmEntity.Uf.SP);
        crm.setMedico(medico);

        when(medicoRepository.existsById(1L)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.create(crm));

        assertEquals("Médico not found: 1", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void createShouldSaveWhenMedicoExists() {
        MedicoEntity medico = new MedicoEntity();
        medico.setId(3L);

        CrmEntity crm = new CrmEntity();
        crm.setCrm("321");
        crm.setUf(CrmEntity.Uf.SP);
        crm.setMedico(medico);

        when(medicoRepository.existsById(3L)).thenReturn(true);
        when(repository.save(any(CrmEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CrmEntity result = service.create(crm);

        assertEquals("321", result.getCrm());
        verify(repository).save(crm);
    }

    @Test
    void createShouldThrowWhenCrmHasInvalidFormat() {
        MedicoEntity medico = new MedicoEntity();
        medico.setId(2L);

        CrmEntity crm = new CrmEntity();
        crm.setCrm("CRM-999");
        crm.setUf(CrmEntity.Uf.SP);
        crm.setMedico(medico);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.create(crm));

        assertEquals("CRM must contain only digits", ex.getMessage());
        verify(medicoRepository, never()).existsById(anyLong());
        verify(repository, never()).save(any());
    }

    @Test
    void updateShouldThrowWhenUfIsMissing() {
        MedicoEntity medicoAtual = new MedicoEntity();
        medicoAtual.setId(1L);

        CrmEntity current = new CrmEntity();
        current.setId(1L);
        current.setCrm("100");
        current.setUf(CrmEntity.Uf.SP);
        current.setMedico(medicoAtual);

        MedicoEntity medicoNovo = new MedicoEntity();
        medicoNovo.setId(2L);

        CrmEntity payload = new CrmEntity();
        payload.setCrm("200");
        payload.setUf(null);
        payload.setMedico(medicoNovo);

        when(repository.findById(1L)).thenReturn(Optional.of(current));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.update(1L, payload));

        assertEquals("UF is required", ex.getMessage());
        verify(medicoRepository, never()).existsById(anyLong());
        verify(repository, never()).save(any());
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
        payload.setCrm("222");
        payload.setUf(CrmEntity.Uf.SP);
        payload.setMedico(medicoNovo);

        when(repository.findById(1L)).thenReturn(Optional.of(current));
        when(medicoRepository.existsById(2L)).thenReturn(true);
        when(repository.save(any(CrmEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CrmEntity result = service.update(1L, payload);

        assertEquals("222", result.getCrm());
        assertEquals(CrmEntity.Uf.SP, result.getUf());
        assertEquals(2L, result.getMedico().getId());
    }
}
