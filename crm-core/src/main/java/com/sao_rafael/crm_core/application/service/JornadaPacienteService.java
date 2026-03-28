package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.JornadaPacienteEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.JornadaPacienteJpaRepository;
import org.springframework.stereotype.Service;

@Service
public class JornadaPacienteService extends BaseCrudService<JornadaPacienteEntity, Long> {

    public JornadaPacienteService(JornadaPacienteJpaRepository repository) {
        super(repository);
    }

    @Override
    protected void copyForUpdate(JornadaPacienteEntity current, JornadaPacienteEntity payload) {

        if (payload.getPaciente() == null) {
            throw new IllegalArgumentException("O Paciente é obrigatório para existir sua jornada");
        }

        current.setPaciente(payload.getPaciente());
        current.setEtapa(payload.getEtapa());

    }

    @Override
    protected String entityName() {
        return "Jornada do Paciente";
    }
}