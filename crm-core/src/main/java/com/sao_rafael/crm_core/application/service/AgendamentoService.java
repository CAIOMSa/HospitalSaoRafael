package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.AgendamentoEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.AgendamentoJpaRepository;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.PacienteJpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AgendamentoService extends BaseCrudService<AgendamentoEntity, Long> {

    public AgendamentoService(AgendamentoJpaRepository repository) {
        super(repository);
    }

    @Override
    protected void copyForUpdate(AgendamentoEntity current, AgendamentoEntity payload) {
        if (payload.getPaciente() == null) {
            throw new IllegalArgumentException("O Paciente é obrigatório");
        }

        if (payload.getMedico() == null) {
            throw new IllegalArgumentException("O Médico é obrigatório");
        }
        current.setPaciente(payload.getPaciente());
        current.setMedico(payload.getMedico());
        current.setStatus(payload.getStatus());
        current.setAtualizadoEm(LocalDateTime.now());
    }

    @Override
    protected String entityName() {
        return "Agendamento";
    }
}