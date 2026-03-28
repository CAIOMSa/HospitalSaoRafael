package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.InternacaoEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.InternacaoJpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InternacaoService extends BaseCrudService<InternacaoEntity, Long> {

    public InternacaoService(InternacaoJpaRepository repository) {
        super(repository);
    }

    @Override
    protected void beforeUpdate(InternacaoEntity current, InternacaoEntity payload) {
        if (payload.getPaciente() == null) {
            throw new IllegalArgumentException("O paciente é obrigatório para acontecer uma internação.");
        }

        if (payload.getMotivo() == null) {
            throw new IllegalArgumentException("É necessário existir um motivo para a internação.");
        }

        if (payload.getLeito() == null) {
            throw new IllegalArgumentException("É necessário ter leito disponível.");
        }
    }

    @Override
    protected void copyForUpdate(InternacaoEntity current, InternacaoEntity payload) {
        current.setPaciente(payload.getPaciente());
        current.setLeito(payload.getLeito());
        current.setStatus(payload.getStatus());
        current.setMotivo(payload.getMotivo());
    }

    @Override
    protected String entityName() {
        return "Internação";
    }
}
