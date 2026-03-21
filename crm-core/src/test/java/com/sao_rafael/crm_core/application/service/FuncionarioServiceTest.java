package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.FuncionarioEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.CargoJpaRepository;
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
class FuncionarioServiceTest {

    @Mock
    private FuncionarioJpaRepository funcionarioRepository;

    @Mock
    private CargoJpaRepository cargoRepository;

    @InjectMocks
    private FuncionarioService service;

    @Test
    void createShouldValidateAndSetDefaults() {
        FuncionarioEntity funcionario = new FuncionarioEntity();
        funcionario.setNome("Joao");
        funcionario.setCpf("123.456.789-00");
        funcionario.setCargoId(10L);

        when(funcionarioRepository.existsByCpf("123.456.789-00")).thenReturn(false);
        when(cargoRepository.existsById(10L)).thenReturn(true);
        when(funcionarioRepository.save(any(FuncionarioEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FuncionarioEntity result = service.create(funcionario);

        assertTrue(result.getAtivo());
        assertNotNull(result.getCriadoEm());
        verify(funcionarioRepository).save(funcionario);
    }

    @Test
    void createShouldThrowWhenCpfAlreadyExists() {
        FuncionarioEntity funcionario = new FuncionarioEntity();
        funcionario.setCpf("123.456.789-00");
        funcionario.setCargoId(1L);

        when(funcionarioRepository.existsByCpf("123.456.789-00")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.create(funcionario));

        assertEquals("CPF already exists: 123.456.789-00", ex.getMessage());
        verify(funcionarioRepository, never()).save(any());
    }

    @Test
    void updateShouldThrowWhenChangingCpfToExistingOne() {
        FuncionarioEntity current = new FuncionarioEntity();
        current.setId(1L);
        current.setCpf("111.111.111-11");

        FuncionarioEntity payload = new FuncionarioEntity();
        payload.setNome("Maria");
        payload.setCpf("222.222.222-22");
        payload.setCargoId(2L);
        payload.setAtivo(true);

        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(current));
        when(cargoRepository.existsById(2L)).thenReturn(true);
        when(funcionarioRepository.existsByCpf("222.222.222-22")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.update(1L, payload));

        assertEquals("CPF already exists: 222.222.222-22", ex.getMessage());
    }
}
