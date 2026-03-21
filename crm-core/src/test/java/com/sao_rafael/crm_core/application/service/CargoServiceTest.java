package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.CargoEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.CargoJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CargoServiceTest {

    @Mock
    private CargoJpaRepository repository;

    @InjectMocks
    private CargoService service;

    @Test
    void createShouldSetDatesAndSave() {
        CargoEntity cargo = new CargoEntity();
        cargo.setCargo("Analista");
        cargo.setDepartamento("TI");
        cargo.setNivelHierarquico("Pleno");

        when(repository.save(any(CargoEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CargoEntity result = service.create(cargo);

        assertNotNull(result.getDataInicio());
        assertNotNull(result.getAtualizadoEm());
        verify(repository, times(1)).save(cargo);
    }

    @Test
    void getByIdShouldThrowWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.getById(99L));

        assertEquals("Cargo not found: 99", ex.getMessage());
    }

    @Test
    void updateShouldCopyFieldsAndSave() {
        CargoEntity current = new CargoEntity();
        current.setId(1L);
        current.setCargo("Antigo");
        current.setDepartamento("Financeiro");
        current.setNivelHierarquico("Junior");
        current.setDataInicio(LocalDateTime.now().minusDays(1));

        CargoEntity payload = new CargoEntity();
        payload.setCargo("Novo");
        payload.setDepartamento("TI");
        payload.setNivelHierarquico("Senior");
        payload.setDataInicio(LocalDateTime.now());

        when(repository.findById(1L)).thenReturn(Optional.of(current));
        when(repository.save(any(CargoEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CargoEntity result = service.update(1L, payload);

        assertEquals("Novo", result.getCargo());
        assertEquals("TI", result.getDepartamento());
        assertEquals("Senior", result.getNivelHierarquico());
        assertNotNull(result.getAtualizadoEm());
        verify(repository, times(1)).save(current);
    }
}
