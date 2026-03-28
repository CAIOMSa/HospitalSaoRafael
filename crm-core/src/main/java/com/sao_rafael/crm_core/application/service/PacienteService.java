package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.PacienteEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.PacienteJpaRepository;
import org.springframework.stereotype.Service;

@Service
public class PacienteService extends BaseCrudService<PacienteEntity, Long> {

    public PacienteService(PacienteJpaRepository repository) {
        super(repository);
    }

    @Override
    protected void copyForUpdate(PacienteEntity current, PacienteEntity payload) {
        current.setNome(payload.getNome());
        current.setCpf(payload.getCpf());
        current.setTelefone(payload.getTelefone());
        current.setEmail(payload.getEmail());
    }

    @Override
    protected String entityName() {
        return "Paciente";
    }
}
