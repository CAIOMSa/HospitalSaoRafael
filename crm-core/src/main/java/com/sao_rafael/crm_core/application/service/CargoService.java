package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.CargoEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.CargoJpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CargoService extends BaseCrudService<CargoEntity, Long> {

    public CargoService(CargoJpaRepository repository) {
        super(repository);
    }

    @Override
    protected void beforeCreate(CargoEntity cargo) {
        if (cargo.getDataInicio() == null) {
            cargo.setDataInicio(LocalDateTime.now());
        }
        cargo.setAtualizadoEm(LocalDateTime.now());
    }

    @Override
    protected void copyForUpdate(CargoEntity current, CargoEntity payload) {
        current.setCargo(payload.getCargo());
        current.setDepartamento(payload.getDepartamento());
        current.setNivelHierarquico(payload.getNivelHierarquico());
        current.setDataInicio(payload.getDataInicio());
    }

    @Override
    protected void beforeUpdate(CargoEntity current, CargoEntity payload) {
        current.setAtualizadoEm(LocalDateTime.now());
    }

    @Override
    protected String entityName() {
        return "Cargo";
    }
}
