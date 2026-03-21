package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.CrmEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.CrmJpaRepository;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.FuncionarioJpaRepository;
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
    private FuncionarioJpaRepository funcionarioRepository;

    @InjectMocks
    private CrmService service;

    @Test
    void createShouldThrowWhenFuncionarioNotFound() {
        CrmEntity crm = new CrmEntity();
        crm.setCrm("CRM-123");
        crm.setIdFuncionario(7L);

        when(funcionarioRepository.existsById(7L)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.create(crm));

        assertEquals("Funcionario not found: 7", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void createShouldSaveWhenFuncionarioExists() {
        CrmEntity crm = new CrmEntity();
        crm.setCrm("CRM-321");
        crm.setIdFuncionario(3L);

        when(funcionarioRepository.existsById(3L)).thenReturn(true);
        when(repository.save(any(CrmEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CrmEntity result = service.create(crm);

        assertEquals("CRM-321", result.getCrm());
        verify(repository).save(crm);
    }

    @Test
    void updateShouldCopyFields() {
        CrmEntity current = new CrmEntity();
        current.setId(1L);
        current.setCrm("OLD");
        current.setIdFuncionario(1L);

        CrmEntity payload = new CrmEntity();
        payload.setCrm("NEW");
        payload.setIdFuncionario(2L);

        when(repository.findById(1L)).thenReturn(Optional.of(current));
        when(funcionarioRepository.existsById(2L)).thenReturn(true);
        when(repository.save(any(CrmEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CrmEntity result = service.update(1L, payload);

        assertEquals("NEW", result.getCrm());
        assertEquals(2L, result.getIdFuncionario());
    }
}
