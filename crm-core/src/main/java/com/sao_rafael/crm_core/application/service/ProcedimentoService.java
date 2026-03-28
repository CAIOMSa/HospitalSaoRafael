package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.ProcedimentoEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.PacienteJpaRepository;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.ProcedimentoJpaRepository;
import org.springframework.stereotype.Service;

@Service
public class ProcedimentoService extends BaseCrudService<ProcedimentoEntity, Long> {

    public ProcedimentoService(ProcedimentoJpaRepository repository) {
        super(repository);
    }

    @Override
    protected void copyForUpdate(ProcedimentoEntity current, ProcedimentoEntity payload) {


        current.setNomeProcedimento(payload.getNomeProcedimento());
        current.setStatus(payload.getStatus());
        current.setStatus(payload.getStatus());

    }

    @Override
    protected String entityName() {
        return "Procedimento";
    }
}
