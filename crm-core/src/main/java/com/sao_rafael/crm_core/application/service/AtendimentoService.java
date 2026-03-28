package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.AtendimentoEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.AtendimentoJpaRepository;
import jakarta.validation.constraints.Null;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AtendimentoService extends BaseCrudService<AtendimentoEntity, Long> {

    public AtendimentoService(AtendimentoJpaRepository repository) {
        super(repository);
    }

    @Override
    protected void copyForUpdate(AtendimentoEntity current, AtendimentoEntity payload) {
        if(payload.getPaciente() == null){
            throw new IllegalArgumentException("O Paciente é obrigatório para o atendimento.");
        }
        if(payload.getMedico() == null){
            throw new IllegalArgumentException("O Médico é obrigatório para o atendimento.");
        }
        current.setPaciente(payload.getPaciente());
        current.setMedico(payload.getMedico());
        current.setEtapa(payload.getEtapa());
        current.setObservacoes(payload.getObservacoes());
        current.setAtualizadoEm(LocalDateTime.now());

    }

    @Override
    protected String entityName() {
        return "Atendimento";
    }
}
